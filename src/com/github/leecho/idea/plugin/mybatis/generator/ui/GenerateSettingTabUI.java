package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.intellij.ide.ui.laf.darcula.ui.DarculaTabbedPaneUI;
import com.intellij.util.ui.JBUI;

/**
 * @author LIQIU
 * created on 2019/6/20
 **/
public class GenerateSettingTabUI extends DarculaTabbedPaneUI {

    @Override
    public void installDefaults(){
        super.installDefaults();
        this.tabInsets = JBUI.insets(5,30);
    }
}
