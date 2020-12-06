package com.github.weylanshi.protobufftaggenerator.services

import com.intellij.openapi.project.Project
import com.github.weylanshi.protobufftaggenerator.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
