package com.example.nnprorocnikovyprojekt.Utility;

import java.util.Collections;
import java.util.List;

public class Utils {
    private Utils(){

    }

    public static <T> List<T> getPage(List<T> sourceList, int pageIndex, int pageSize) {
        if(pageSize <= 0 || pageIndex < 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (pageIndex) * pageSize;
        if(sourceList == null || sourceList.size() <= fromIndex){
            return Collections.emptyList();
        }

        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }
}
