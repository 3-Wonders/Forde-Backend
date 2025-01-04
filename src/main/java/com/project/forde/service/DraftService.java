package com.project.forde.service;

import com.project.forde.dto.draft.DraftDto;
import com.project.forde.entity.*;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.repository.*;
import com.project.forde.type.ImagePathEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DraftService {
    private final DraftRepository draftRepository;
    private final AppUserRepository appUserRepository;
    private final TagRepository tagRepository;
    private final DraftTagRepository draftTagRepository;
    private final BoardImageRepository boardImageRepository;

    private final TagService tagService;
    private final DraftTagService draftTagService;
    private final BoardImageService boardImageService;
    private final FileService fileService;

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

        List<Tag> tags = tagService.increaseTagCount(request.getTagIds());
        List<DraftTag> draftTags = draftTagService.createDraftTag(createdDraft, tags);

        tagRepository.saveAll(tags);
        draftTagRepository.saveAll(draftTags);

        List<BoardImage> dummyImages = boardImageService.createImages(createdDraft, request.getImageIds());
        boardImageRepository.saveAll(dummyImages);

        fileService.processThumbnailAndSave(
                request.getThumbnail(),
                ImagePathEnum.BOARD.getPath(),
                createdDraft,
                draftRepository::save
        );
    }
}
