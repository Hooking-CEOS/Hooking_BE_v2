package shop.hooking.hooking.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CopySearchResponse {
    private List<CopySearchResult> data;

    // Constructors, getters, setters, and other methods
}