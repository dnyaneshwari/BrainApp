package com.nishitadutta.brainapp.Objects;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by Nishita on 05-09-2016.
 */
public class BrainResponse {
    public String cnt;

    public BrainResponse(String cnt) {
        this.cnt = cnt;
    }

    public BrainResponse() {
    }

    public Spanned getCnt() {
        Spanned htmlCnt= Html.fromHtml(cnt);
        return htmlCnt;
    }
}

