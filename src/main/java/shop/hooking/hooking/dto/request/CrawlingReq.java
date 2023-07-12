package shop.hooking.hooking.dto.request;

import java.util.List;

public class CrawlingReq {
    private List<CrawlingData> data;

    public List<CrawlingData> getData() {
        return data;
    }

    public void setData(List<CrawlingData> data) {
        this.data = data;
    }
}
