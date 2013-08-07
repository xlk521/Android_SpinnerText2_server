package com.tky.server;

public class FileLog {
	 private Long id;
     private String path;
     public FileLog(Long id, String path) {
         super();
         this.id = id;
         this.path = path;
     }

     public Long getId() {
         return id;
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getPath() {
         return path;
     }
//
     public void setPath(String path) {
         this.path = path;
     }
}
