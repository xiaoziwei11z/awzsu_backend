package com.awzsu.common.utils;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

public class IKUtil {
    public static String split(String content,String splitChar) throws IOException {
        StringReader stringReader = new StringReader(content);
        IKSegmenter ikSegmenter = new IKSegmenter(stringReader,true);
        Lexeme lexeme;
        StringBuilder stringBuilder = new StringBuilder("");
        while ((lexeme=ikSegmenter.next())!=null){
            stringBuilder.append(lexeme.getLexemeText()+splitChar);
        }
        return stringBuilder.toString();
    }
}
