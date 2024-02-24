package shop.hooking.hooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Folder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FolderResDto {
    private List<Folder> folders;

    @Builder
    public FolderResDto(List<Folder> folders){
        this.folders=folders;

    }
}
