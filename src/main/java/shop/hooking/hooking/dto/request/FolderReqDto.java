package shop.hooking.hooking.dto.request;

import lombok.Builder;
import lombok.Data;
import shop.hooking.hooking.entity.Folder;

import java.util.List;

@Data
@Builder
public class FolderReqDto {
    private Long folderId;

    @Builder
    public FolderReqDto(Long folderId){
        this.folderId=folderId;

    }
}
