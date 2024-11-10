package juniper.monotone.config;

//for some reaason, owo-config cannot properly handle generating file packages
//so until then, manualy move the file to the right package to remove language server errors

import io.wispforest.owo.config.annotation.Config;

@Config(name = "monotone_config", wrapperName = "MonotoneConfig")
public class MonotoneConfigModel {
    public int temp;
    public boolean temp2;
}
