package com.blockexplore.utils;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ImageUtil {

    // 写入自定义数据到图片
    public static byte[] writeCustomData(BufferedImage buffImg, Map<String, String> data) throws Exception {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);

        // 添加元数据
        IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
        IIOMetadataNode text = new IIOMetadataNode("tEXt");

        // 遍历数据并添加到元数据
        for (Map.Entry<String, String> entry : data.entrySet()) {
            IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
            textEntry.setAttribute("keyword", entry.getKey());
            textEntry.setAttribute("value", new String(entry.getValue().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)); // 使用 ISO_8859_1 编码
            text.appendChild(textEntry);
        }

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
        root.appendChild(text);
        metadata.mergeTree("javax_imageio_png_1.0", root);

        // 写入数据
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream stream = ImageIO.createImageOutputStream(baos);
        writer.setOutput(stream);
        writer.write(metadata, new IIOImage(buffImg, null, metadata), writeParam);
        stream.close();

        return baos.toByteArray();
    }

    // 读取自定义数据
    public static String readCustomData(byte[] imageData, String key) throws IOException {
        ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();
        imageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(imageData)), true);

        // 读取元数据
        IIOMetadata metadata = imageReader.getImageMetadata(0);
        IIOMetadataNode textNode = (IIOMetadataNode) metadata.getAsTree("javax_imageio_png_1.0");
        if (textNode != null) {
            for (int i = 0; i < textNode.getLength(); i++) {
                IIOMetadataNode childNode = (IIOMetadataNode) textNode.getChildNodes().item(i);
                if ("tEXt".equals(childNode.getNodeName())) {
                    for (int j = 0; j < childNode.getLength(); j++) {
                        IIOMetadataNode entryNode = (IIOMetadataNode) childNode.getChildNodes().item(j);
                        String keyword = entryNode.getAttribute("keyword");
                        if (key.equals(keyword)) {
                            // 使用 UTF-8 编码读取
                            return new String(entryNode.getAttribute("value").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        }
                    }
                }
            }
        }
        return null; // 如果没有找到对应的key
    }
}
