package com.project.forde.service;

import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.draft.DraftDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.*;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.DraftMapper;
import com.project.forde.repository.*;
import com.project.forde.type.ImageActionEnum;
import com.project.forde.type.ImagePathEnum;
import com.project.forde.util.CustomTimestamp;
import com.project.forde.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DraftService {
    private final DraftRepository draftRepository;
    private final DraftTagRepository draftTagRepository;
    private final BoardImageRepository boardImageRepository;
    private final AppUserRepository appUserRepository;

    private final AppUserService appUserService;
    private final TagService tagService;
    private final DraftTagService draftTagService;
    private final BoardImageService boardImageService;
    private final FileService fileService;

    private final FileStore fileStore;

    @UserVerify
    public List<DraftDto.Response.Draft> getDrafts() {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        List<Draft> drafts = draftRepository.findTop10ByUploaderOrderByDraftIdDesc(user);
        List<DraftTag> draftTags = draftTagRepository.findAllByDraftTagPK_DraftIn(drafts);
        List<BoardImage> boardImages = boardImageRepository.findAllByDraftIn(drafts);

        return drafts.stream().map(
                draft -> {
                    List<Long> imageIds = boardImages.stream().filter(
                            boardImage -> boardImage.getDraft().equals(draft)
                    ).map(BoardImage::getImageId).toList();

                    List<TagDto.Response.TagWithoutCount> tags = draftTags.stream().filter(
                            draftTag -> draftTag.getDraftTagPK().getDraft().equals(draft)
                    ).map(draftTag -> new TagDto.Response.TagWithoutCount(
                                draftTag.getDraftTagPK().getTag().getTagId(),
                                draftTag.getDraftTagPK().getTag().getTagName()
                    )).toList();

                    return DraftMapper.INSTANCE.toDraft(
                            draft,
                            tags,
                            imageIds
                    );
                }
        ).toList();
    }

    @Transactional
    @UserVerify
    public void create(final DraftDto.Request.Create request) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Long draftTotal = draftRepository.countByUploader(user);
        if (draftTotal >= 10) {
            throw new CustomException(ErrorCode.TOO_MANY_DRAFT);
        }

        Draft draft = Draft.builder()
                .category(request.getBoardType().charAt(0))
                .uploader(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        Draft createdDraft = draftRepository.save(draft);

        List<Tag> tags = tagService.getTagsWithIds(request.getTagIds());
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            draftTagService.createDraftTag(createdDraft, tags);
        }
        boardImageService.createImages(createdDraft, request.getImageIds());

        fileService.processThumbnailAndSave(
                request.getThumbnail(),
                ImagePathEnum.BOARD.getPath(),
                createdDraft,
                draftRepository::save
        );
    }

    @Transactional
    @UserVerify
    public void update(final Long draftId, final DraftDto.Request.Update request) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DRAFT));
        String oldThumbnailPath = draft.getThumbnailPath();

        if (!draft.getUploader().equals(user)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_DRAFT);
        }

        draftTagService.updateDiffTags(draft, request.getTagIds());
        boardImageService.updateDiffImages(draft, request.getImageIds());

        draft.setCategory(request.getBoardType().charAt(0));
        draft.setTitle(request.getTitle());
        draft.setContent(request.getContent());
        draft.setUpdatedTime(new CustomTimestamp().getTimestamp());

        if (request.getThumbnailAction().equals(ImageActionEnum.UPLOAD.getType())) {
            fileService.processThumbnailAndSave(
                    request.getThumbnail(),
                    ImagePathEnum.BOARD.getPath(),
                    draft,
                    draftRepository::save
            );

            // TODO: Kafka 또는 무언가를 사용하여 Topic을 발생시키고, 삭제할 이미지를 모아서 삭제
            if (oldThumbnailPath != null) {
                fileStore.deleteFile(oldThumbnailPath);
            }
        } else if (request.getThumbnailAction().equals(ImageActionEnum.DELETE.getType())) {
            draft.setThumbnailPath(null);
            draft.setThumbnailSize(null);
            draft.setThumbnailType(null);

            // TODO: Kafka 또는 무언가를 사용하여 Topic을 발생시키고, 삭제할 이미지를 모아서 삭제
            if (oldThumbnailPath != null) {
                fileStore.deleteFile(oldThumbnailPath);
            }
        }

        draftRepository.save(draft);
    }

    @Transactional
    @UserVerify
    public void delete(final Long draftId) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser user = appUserService.getUser(userId);

        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DRAFT));

        if (!draft.getUploader().equals(user)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_DRAFT);
        }

        List<DraftTag> draftTags = draftTagRepository.findAllByDraftTagPK_Draft(draft);
        draftTagRepository.deleteAllInBatch(draftTags);
        draftRepository.delete(draft);

        List<BoardImage> boardImages = boardImageRepository.findAllByDraft(draft);
        List<String> imagePaths = new ArrayList<>(boardImages.stream().map(BoardImage::getImagePath).toList());
        boardImageRepository.deleteAll(boardImages);

        if (draft.getThumbnailPath() != null) {
            fileStore.deleteFile(draft.getThumbnailPath());
        }

        imagePaths.forEach(fileStore::deleteFile);
    }
}
