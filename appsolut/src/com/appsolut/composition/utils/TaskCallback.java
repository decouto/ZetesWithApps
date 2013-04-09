package com.appsolut.composition.utils;

import android.content.Intent;

public interface TaskCallback {
    
    void done( Intent callbackIntent, Boolean finish );

}
