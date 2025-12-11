package com.erickwu.backend.model;

import java.util.List;

/**
 * 分页结果包装类
 */
public class PageResult<T> {
    
    /**
     * 当前页码
     */
    private int page;
    
    /**
     * 每页数量
     */
    private int size;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 总页数
     */
    private int totalPages;
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    public PageResult() {}
    
    public PageResult(int page, int size, long total, List<T> list) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.list = list;
    }
    
    // Getters and Setters
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public long getTotal() {
        return total;
    }
    
    public void setTotal(long total) {
        this.total = total;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public List<T> getList() {
        return list;
    }
    
    public void setList(List<T> list) {
        this.list = list;
    }
    
    public static <T> PageResult<T> of(int page, int size, long total, List<T> list) {
        return new PageResult<>(page, size, total, list);
    }
}
