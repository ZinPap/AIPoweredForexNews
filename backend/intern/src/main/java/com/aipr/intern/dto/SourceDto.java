package com.aipr.intern.dto;

public class SourceDto {
    private String name;
    private String defaultCategory;
    private String defaultContentType;

    public SourceDto() {
    }

    public SourceDto(String name, String defaultCategory, String defaultContentType) {
        this.name = name;
        this.defaultCategory = defaultCategory;
        this.defaultContentType = defaultContentType;
    }

   public String getName() {
        return name;
   }
   public void setName(String name) {
        this.name = name;
   }
   public String getDefaultCategory() {
        return defaultCategory;
   }
   public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
   }
   public String getDefaultContentType() {
        return defaultContentType;
   }
   public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
   }

}