package com.yao.masterspringbatch.listener;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jack Yao on 2021/12/5 8:55 PM
 */
public class ProductSkipListener {

    private String readErrorFileName="error/read_skipped";
    private String processErrorFileName="error/process_skipped";

    @OnSkipInProcess
    public void onSkipProcess(Object item,Throwable t){
        if ( t instanceof RuntimeException){/*這個錯誤有行數或字串可以給出*/
            onSkip(item,processErrorFileName);
        }
    }


    @OnSkipInRead/*當reader有錯誤就會觸發這邊*/
    public void onSkipRead(Throwable t){
        if ( t instanceof FlatFileParseException){/*這個錯誤有行數或字串可以給出*/
            FlatFileParseException ffpe = (FlatFileParseException) t;
            onSkip(ffpe.getInput(),readErrorFileName);
        }
        /*這邊做一個物件用來丟到錯誤檔案*/
    }
    @OnSkipInWrite/*當write有錯誤就會觸發這邊*/
    public void onSkipWrite(Object item,Throwable t){
        if ( t instanceof RuntimeException){/*這個錯誤有行數或字串可以給出*/
            onSkip(item,processErrorFileName);
        }
    }

    public void onSkip(Object o,String fname){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fname, true);
            fos.write(o.toString().getBytes());
            fos.write("\r\n".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
