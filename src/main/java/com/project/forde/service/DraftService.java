package com.project.forde.service;

import com.project.forde.dto.draft.DraftDto;
import com.project.forde.entity.*;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.*;
import com.project.forde.type.ImageActionEnum;
import com.project.forde.type.ImagePathEnum;
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
    private final AppUserRepository appUserRepository;
    private final BoardImageRepository boardImageRepository;

    private final TagService tagService;
    private final DraftTagService draftTagService;
    private final BoardImageService boardImageService;
    private final FileService fileService;

    private final FileStore fileStore;

    @Transactional
    public void create(final Long userId, final DraftDto.Request request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

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

        List<Tag> tags = tagService.getTags(request.getTagIds());
        draftTagService.createDraftTag(createdDraft, tags);
        boardImageService.createImages(createdDraft, request.getImageIds());

        fileService.processThumbnailAndSave(
                request.getThumbnail(),
                ImagePathEnum.BOARD.getPath(),
                createdDraft,
                draftRepository::save
        );
    }

    @Transactional
    public void update(final Long userId, final Long draftId, final DraftDto.Request request) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DRAFT));
        String oldThumbnailPath = draft.getThumbnailPath();

        if (!draft.getUploader().equals(user)) {
            throw new CustomException(ErrorCode.NOT_MATCHED_DRAFT);
        }

        draftTagService.updateDiffTags(draft, request.getTagIds());
        boardImageService.updateDiffImages(draft, request.getImageIds());

        draft.setTitle(request.getTitle());
        draft.setContent(request.getContent());

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
    public void delete(final Long userId, final Long draftId) {
        AppUser user = appUserRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

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
