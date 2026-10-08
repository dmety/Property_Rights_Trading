package com.example.app.components;

public class ProjectItem {
    private String projectName;
    private String projectDescription;

    public ProjectItem(String projectName, String projectDescription) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }
}
