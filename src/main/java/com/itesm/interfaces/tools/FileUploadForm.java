package com.itesm.interfaces.tools;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

public class FileUploadForm {

    @FormParam("fileContent")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream fileContent;
}