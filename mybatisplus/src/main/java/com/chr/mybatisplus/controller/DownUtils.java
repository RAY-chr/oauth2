package com.chr.mybatisplus.controller;

import org.apache.zookeeper.common.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author RAY
 * @descriptions
 * @since 2020/2/23
 */
public class DownUtils {


    public static void downFile(HttpServletRequest request,HttpServletResponse response, String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
        File readFile = new File(filePath);
        InputStream inputStream = null;
        ServletOutputStream out = null;
        try {
            long pos = GetPos(response, request);
            SetHttpHreader(response,readFile.getName(),readFile.length(),pos);
            inputStream = new FileInputStream(filePath);
            out = response.getOutputStream();
            if (pos > 0) {
                inputStream.skip(pos);
            }
            byte[] buffer = new byte[1024 * 10];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(inputStream);
        }
    }

    /**
     * 获取位置信息
     *
     * @param response
     * @param request
     * @return
     */
    public static long GetPos(HttpServletResponse response, HttpServletRequest request) {
        long pos = 0;
        if (null != request.getHeader("Range")) {
            // 断点续传
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            try {
                String range = request.getHeader("Range");
                System.out.println(range);
                range.replaceAll("bytes=", "").replaceAll("-", "");
                System.out.println(range);
                pos = Long.parseLong(range);
            } catch (NumberFormatException e) {
                pos = 0;
            }
        }
        return pos;
    }

    /**
     * 设置头
     *
     * @param response 响应
     * @param pos pos
     */
    public static void SetHttpHreader(
            HttpServletResponse response, String fileName, long fileSize, long pos) {

        String odexName = fileName;

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/x-download");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(fileSize));

        String outname =
                new String(odexName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        response.setHeader("Content-Disposition", "attachment;fileName=" + outname);

        StringBuilder contentRange = new StringBuilder();
        contentRange.append("bytes ");
        contentRange.append(pos + "").append("-");
        contentRange.append((fileSize - 1) + "").append("/");
        contentRange.append(fileSize + "");
        System.out.println(contentRange.toString());
        response.setHeader("Content-Range", contentRange.toString());
    }
}
