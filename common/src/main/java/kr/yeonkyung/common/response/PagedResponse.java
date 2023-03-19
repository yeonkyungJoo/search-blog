package kr.yeonkyung.common.response;

import java.util.Collections;
import java.util.List;

public class PagedResponse<T> {

    static class Meta {
        private Integer total;
        private Integer page;
        private Integer size;

        Meta(Integer total, Integer page, Integer size) {
            this.total = total;
            this.page = page;
            this.size = size;
        }

        public Integer getTotal() {
            return total;
        }

        public Integer getPage() {
            return page;
        }

        public Integer getSize() {
            return size;
        }
    }

    private Meta meta;
    private List<T> items;

    public PagedResponse(Integer total, Integer page, Integer size, List<T> items) {
        this.meta = new Meta(total, page, size);
        this.items = items;
    }

    public Meta getMeta() {
        return meta;
    }

    public List<T> getItems() {
        return items;
    }

    public static <T> PagedResponse<T> noItems() {
        return new PagedResponse<>(0, 1, 0, Collections.emptyList());
    }
}
