package com.cnezsoft.zentao.colorswatch;

import java.util.HashMap;

/**
 * Created by Catouse on 2015/1/29.
 */
public enum MaterialColorSwatch {
    Red(new String[]{"#FFEBEE","#FFCDD2","#EF9A9A","#E57373","#EF5350","i#F44336","i#E53935","I#D32F2F","I#C62828","I#B71C1C","#FF8A80","I#FF5252","I#FF1744","I#D50000"}),
    Pink(new String[]{"#FCE4EC","#F8BBD0","#F48FB1","#F06292","#EC407A","I#E91E63","I#D81B60","I#C2185B","I#AD1457","I#880E4F","#FF80AB","I#FF4081","I#F50057","I#C51162"}),
    Purple(new String[]{"#F3E5F5","#E1BEE7","#CE93D8","I#BA68C8","I#AB47BC","I#9C27B0","I#8E24AA","I#7B1FA2","I#6A1B9A","I#4A148C","#EA80FC","I#E040FB","I#D500F9","I#AA00FF"}),
    DeepPurple(new String[]{"#EDE7F6","#D1C4E9","#B39DDB","I#9575CD","I#7E57C2","I#673AB7","I#5E35B1","I#512DA8","I#4527A0","I#311B92","#B388FF","I#7C4DFF","I#651FFF","I#6200EA"}),
    Indigo(new String[]{"#E8EAF6","#C5CAE9","#9FA8DA","I#7986CB","I#5C6BC0","I#3F51B5","I#3949AB","I#303F9F","I#283593","I#1A237E","#8C9EFF","I#536DFE","I#3D5AFE","I#304FFE"}),
    Blue(new String[]{"#E3F2FD","#BBDEFB","#90CAF9","#64B5F6","#42A5F5","I#2196F3","I#1E88E5","I#1976D2","I#1565C0","I#0D47A1","#82B1FF","I#448AFF","I#2979FF","I#2962FF"}),
    LightBlue(new String[]{"#E1F5FE","#B3E5FC","#81D4FA","#4FC3F7","#29B6F6","I#03A9F4","I#039BE5","I#0288D1","I#0277BD","I#01579B","#80D8FF","#40C4FF","#00B0FF","I#0091EA"}),
    Cyan(new String[]{"#E0F7FA","#B2EBF2","#80DEEA","#4DD0E1","#26C6DA","I#00BCD4","I#00ACC1","I#0097A7","I#00838F","I#006064","#84FFFF","#18FFFF","#00E5FF","#00B8D4"}),
    Teal(new String[]{"#E0F2F1","#B2DFDB","#80CBC4","#4DB6AC","#26A69A","I#009688","I#00897B","I#00796B","I#00695C","I#004D40","#A7FFEB","#64FFDA","#1DE9B6","#00BFA5"}),
    Green(new String[]{"#D0F8CE","#A3E9A4","#72D572","#42BD41","#2BAF2B","I#259B24","I#0A8F08","I#0A7E07","I#056F00","I#0D5302","#A2F78D","#5AF158","#14E715","#12C700"}),
    LightGreen(new String[]{"#F1F8E9","#DCEDC8","#C5E1A5","#AED581","#9CCC65","#8BC34A","#7CB342","#689F38","I#558B2F","I#33691E","#CCFF90","#B2FF59","#76FF03","#64DD17"}),
    Lime(new String[]{"#F9FBE7","#F0F4C3","#E6EE9C","#DCE775","#D4E157","#CDDC39","#C0CA33","#AFB42B","#9E9D24","I#827717","#F4FF81","#EEFF41","#C6FF00","#AEEA00"}),
    Yellow(new String[]{"#FFFDE7","#FFF9C4","#FFF59D","#FFF176","#FFEE58","#FFEB3B","#FDD835","#FBC02D","#F9A825","#F57F17","#FFFF8D","#FFFF00","#FFEA00","#FFD600"}),
    Amber(new String[]{"#FFF8E1","#FFECB3","#FFE082","#FFD54F","#FFCA28","#FFC107","#FFB300","#FFA000","#FF8F00","#FF6F00","#FFE57F","#FFD740","#FFC400","#FFAB00"}),
    Orange(new String[]{"#FFF3E0","#FFE0B2","#FFCC80","#FFB74D","#FFA726","#FF9800","#FB8C00","#F57C00","I#EF6C00","I#E65100","#FFD180","#FFAB40","#FF9100","#FF6D00"}),
    DeepOrange(new String[]{"#FBE9E7","#FFCCBC","#FFAB91","#FF8A65","#FF7043","I#FF5722","I#F4511E","I#E64A19","I#D84315","I#BF360C","#FF9E80","#FF6E40","I#FF3D00","I#DD2C00"}),
    Brown(new String[]{"#EFEBE9","#D7CCC8","#BCAAA4","I#A1887F","I#8D6E63","I#795548","I#6D4C41","I#5D4037","I#4E342E","I#3E2723", "#D7CCC8", "#BCAAA4", "I#6D4C41", "I#4E342E"}),
    Grey(new String[]{"#FAFAFA","#F5F5F5","#EEEEEE","#E0E0E0","#BDBDBD","#9E9E9E","I#757575","I#616161","I#424242","I#212121", "#F1F1F1", "#EEEEEE", "#9E9E9E", "I#333333"}),
    BlueGrey(new String[]{"#ECEFF1","#CFD8DC","#B0BEC5","#90A4AE","I#78909C","I#607D8B","I#546E7A","I#455A64","I#37474F","I#263238", "#CFD8DC", "#B0BEC5", "I#78909C", "I#455A64"});

    private HashMap<MaterialColorName, MaterialColor> swatch;

    public MaterialColor color(MaterialColorName name) {
        return swatch.get(name);
    }

    public MaterialColor primary() {
        return swatch.get(MaterialColorName.C500);
    }

    MaterialColorSwatch(String[] hexes) {
        swatch = new HashMap<>(hexes.length);
        MaterialColorName[] names = MaterialColorName.values();

        for(int i = 0; i < hexes.length; ++i) {
            swatch.put(names[i], new MaterialColor(hexes[i]));
        }
    }

    public static final MaterialColorSwatch[] PriAccentSwatches = new MaterialColorSwatch[]{
            Grey,
            Red,
            DeepOrange,
            Purple,
            Blue
    };
}
