//
//  FontAwesome.swift
//  zentao
//
//  Created by Sun Hao on 15/3/13.
//  Copyright (c) 2015年 cnezsoft.com. All rights reserved.
//

import Foundation
import UIKit

typealias IconVal = UniChar

extension IconVal {
    var text: String {
        get {
            return String(format: "%C", self)
        }
    }
}

struct FontIcon {
    
    static let name = "FontAwesome"
    
    static func fontWithSize(size:CGFloat) -> UIFont {
        return UIFont(name: name, size: size)!
    }
    
    static func label(inout label: UILabel,  setIcon icon: IconVal, size: CGFloat, color: UIColor, sizeToFit shouldSizeToFit: Bool)  {
        
        label.font = self.fontWithSize(size);
        label.text = icon.text;
        label.textColor = color;
        
        label.backgroundColor = UIColor.clearColor();
        
        if (shouldSizeToFit) {
            label.sizeToFit()
        }
        
        // NOTE: ionicons will be silent through VoiceOver, but the Label is still selectable through VoiceOver. This can cause a usability issue because a visually impaired user might navigate to the label but get no audible feedback that the navigation happened. So hide the label for VoiceOver by default - if your label should be descriptive, un-hide it explicitly after creating it, and then set its accessibiltyLabel.
        label.accessibilityElementsHidden = true;
    }
    
    static func labelWithIcon(#iconName: IconVal, size: CGFloat, color: UIColor) -> UILabel {
        var label = UILabel()
        self.label(&label, setIcon: iconName, size: size, color: color, sizeToFit: true)
        return label
    }
    
    static func imageWithIcon(iconName: IconVal, size: CGFloat, color: UIColor) -> UIImage {
        
        return imageWithIcon(iconName: iconName,  iconSize:size,  iconColour: color, imageSize: CGSizeMake(size, size));
        
    }
    
    static func imageWithIcon(#iconName: IconVal, iconSize: CGFloat, iconColour: UIColor = UIColor.blackColor(), imageSize: CGSize) -> UIImage {
        
        if ((UIDevice.currentDevice().systemVersion as NSString).floatValue < 6) {
            NSLog("[ IonIcons ] Incompatible system version.")
            return UIImage()
        }
        
        var style = NSMutableParagraphStyle()
        style.alignment = NSTextAlignment.Left
        style.baseWritingDirection = NSWritingDirection.LeftToRight
        
        UIGraphicsBeginImageContextWithOptions(imageSize, false, 0.0);
        
        var attString = NSAttributedString(string: iconName.text, attributes: [NSFontAttributeName:  fontWithSize(iconSize), NSForegroundColorAttributeName: iconColour, NSParagraphStyleAttributeName: style])
        
        // get the target bounding rect in order to center the icon within the UIImage:
        var ctx = NSStringDrawingContext()
        var boundingRect = attString.boundingRectWithSize(CGSizeMake(iconSize, iconSize), options: NSStringDrawingOptions.UsesDeviceMetrics, context: ctx)
        
        attString.drawInRect(CGRectMake((imageSize.width/2.0) - boundingRect.size.width/2.0, (imageSize.height/2.0) - boundingRect.size.height/2.0, imageSize.width, imageSize.height))
        
        var iconImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        if(iconImage.respondsToSelector(Selector("imageWithRenderingMode:"))){
            iconImage = iconImage.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal)
        }
        
        return iconImage
    }
    
    static let adjust: IconVal = 0xf042
    static let adn: IconVal = 0xf170
    static let align_center: IconVal = 0xf037
    static let align_justify: IconVal = 0xf039
    static let align_left: IconVal = 0xf036
    static let align_right: IconVal = 0xf038
    static let ambulance: IconVal = 0xf0f9
    static let anchor: IconVal = 0xf13d
    static let android: IconVal = 0xf17b
    static let angellist: IconVal = 0xf209
    static let angle_double_down: IconVal = 0xf103
    static let angle_double_left: IconVal = 0xf100
    static let angle_double_right: IconVal = 0xf101
    static let angle_double_up: IconVal = 0xf102
    static let angle_down: IconVal = 0xf107
    static let angle_left: IconVal = 0xf104
    static let angle_right: IconVal = 0xf105
    static let angle_up: IconVal = 0xf106
    static let apple: IconVal = 0xf179
    static let archive: IconVal = 0xf187
    static let area_chart: IconVal = 0xf1fe
    static let arrow_circle_down: IconVal = 0xf0ab
    static let arrow_circle_left: IconVal = 0xf0a8
    static let arrow_circle_o_down: IconVal = 0xf01a
    static let arrow_circle_o_left: IconVal = 0xf190
    static let arrow_circle_o_right: IconVal = 0xf18e
    static let arrow_circle_o_up: IconVal = 0xf01b
    static let arrow_circle_right: IconVal = 0xf0a9
    static let arrow_circle_up: IconVal = 0xf0aa
    static let arrow_down: IconVal = 0xf063
    static let arrow_left: IconVal = 0xf060
    static let arrow_right: IconVal = 0xf061
    static let arrow_up: IconVal = 0xf062
    static let arrows: IconVal = 0xf047
    static let arrows_alt: IconVal = 0xf0b2
    static let arrows_h: IconVal = 0xf07e
    static let arrows_v: IconVal = 0xf07d
    static let asterisk: IconVal = 0xf069
    static let at: IconVal = 0xf1fa
    static let automobile: IconVal = 0xf1b9
    static let backward: IconVal = 0xf04a
    static let ban: IconVal = 0xf05e
    static let bank: IconVal = 0xf19c
    static let bar_chart: IconVal = 0xf080
    static let bar_chart_o: IconVal = 0xf080
    static let barcode: IconVal = 0xf02a
    static let bars: IconVal = 0xf0c9
    static let bed: IconVal = 0xf236
    static let beer: IconVal = 0xf0fc
    static let behance: IconVal = 0xf1b4
    static let behance_square: IconVal = 0xf1b5
    static let bell: IconVal = 0xf0f3
    static let bell_o: IconVal = 0xf0a2
    static let bell_slash: IconVal = 0xf1f6
    static let bell_slash_o: IconVal = 0xf1f7
    static let bicycle: IconVal = 0xf206
    static let binoculars: IconVal = 0xf1e5
    static let birthday_cake: IconVal = 0xf1fd
    static let bitbucket: IconVal = 0xf171
    static let bitbucket_square: IconVal = 0xf172
    static let bitcoin: IconVal = 0xf15a
    static let bold: IconVal = 0xf032
    static let bolt: IconVal = 0xf0e7
    static let bomb: IconVal = 0xf1e2
    static let book: IconVal = 0xf02d
    static let bookmark: IconVal = 0xf02e
    static let bookmark_o: IconVal = 0xf097
    static let briefstatic: IconVal = 0xf0b1
    static let btc: IconVal = 0xf15a
    static let bug: IconVal = 0xf188
    static let building: IconVal = 0xf1ad
    static let building_o: IconVal = 0xf0f7
    static let bullhorn: IconVal = 0xf0a1
    static let bullseye: IconVal = 0xf140
    static let bus: IconVal = 0xf207
    static let buysellads: IconVal = 0xf20d
    static let cab: IconVal = 0xf1ba
    static let calculator: IconVal = 0xf1ec
    static let calendar: IconVal = 0xf073
    static let calendar_o: IconVal = 0xf133
    static let camera: IconVal = 0xf030
    static let camera_retro: IconVal = 0xf083
    static let car: IconVal = 0xf1b9
    static let caret_down: IconVal = 0xf0d7
    static let caret_left: IconVal = 0xf0d9
    static let caret_right: IconVal = 0xf0da
    static let caret_square_o_down: IconVal = 0xf150
    static let caret_square_o_left: IconVal = 0xf191
    static let caret_square_o_right: IconVal = 0xf152
    static let caret_square_o_up: IconVal = 0xf151
    static let caret_up: IconVal = 0xf0d8
    static let cart_arrow_down: IconVal = 0xf218
    static let cart_plus: IconVal = 0xf217
    static let cc: IconVal = 0xf20a
    static let cc_amex: IconVal = 0xf1f3
    static let cc_discover: IconVal = 0xf1f2
    static let cc_mastercard: IconVal = 0xf1f1
    static let cc_paypal: IconVal = 0xf1f4
    static let cc_stripe: IconVal = 0xf1f5
    static let cc_visa: IconVal = 0xf1f0
    static let certificate: IconVal = 0xf0a3
    static let chain: IconVal = 0xf0c1
    static let chain_broken: IconVal = 0xf127
    static let check: IconVal = 0xf00c
    static let check_circle: IconVal = 0xf058
    static let check_circle_o: IconVal = 0xf05d
    static let check_square: IconVal = 0xf14a
    static let check_square_o: IconVal = 0xf046
    static let chevron_circle_down: IconVal = 0xf13a
    static let chevron_circle_left: IconVal = 0xf137
    static let chevron_circle_right: IconVal = 0xf138
    static let chevron_circle_up: IconVal = 0xf139
    static let chevron_down: IconVal = 0xf078
    static let chevron_left: IconVal = 0xf053
    static let chevron_right: IconVal = 0xf054
    static let chevron_up: IconVal = 0xf077
    static let child: IconVal = 0xf1ae
    static let circle: IconVal = 0xf111
    static let circle_o: IconVal = 0xf10c
    static let circle_o_notch: IconVal = 0xf1ce
    static let circle_thin: IconVal = 0xf1db
    static let clipboard: IconVal = 0xf0ea
    static let clock_o: IconVal = 0xf017
    static let close: IconVal = 0xf00d
    static let cloud: IconVal = 0xf0c2
    static let cloud_download: IconVal = 0xf0ed
    static let cloud_upload: IconVal = 0xf0ee
    static let cny: IconVal = 0xf157
    static let code: IconVal = 0xf121
    static let code_fork: IconVal = 0xf126
    static let codepen: IconVal = 0xf1cb
    static let coffee: IconVal = 0xf0f4
    static let cog: IconVal = 0xf013
    static let cogs: IconVal = 0xf085
    static let columns: IconVal = 0xf0db
    static let comment: IconVal = 0xf075
    static let comment_o: IconVal = 0xf0e5
    static let comments: IconVal = 0xf086
    static let comments_o: IconVal = 0xf0e6
    static let compass: IconVal = 0xf14e
    static let compress: IconVal = 0xf066
    static let connectdevelop: IconVal = 0xf20e
    static let copy: IconVal = 0xf0c5
    static let copyright: IconVal = 0xf1f9
    static let credit_card: IconVal = 0xf09d
    static let crop: IconVal = 0xf125
    static let crosshairs: IconVal = 0xf05b
    static let css3: IconVal = 0xf13c
    static let cube: IconVal = 0xf1b2
    static let cubes: IconVal = 0xf1b3
    static let cut: IconVal = 0xf0c4
    static let cutlery: IconVal = 0xf0f5
    static let dashboard: IconVal = 0xf0e4
    static let dashcube: IconVal = 0xf210
    static let database: IconVal = 0xf1c0
    static let dedent: IconVal = 0xf03b
    static let delicious: IconVal = 0xf1a5
    static let desktop: IconVal = 0xf108
    static let deviantart: IconVal = 0xf1bd
    static let diamond: IconVal = 0xf219
    static let digg: IconVal = 0xf1a6
    static let dollar: IconVal = 0xf155
    static let dot_circle_o: IconVal = 0xf192
    static let download: IconVal = 0xf019
    static let dribbble: IconVal = 0xf17d
    static let dropbox: IconVal = 0xf16b
    static let drupal: IconVal = 0xf1a9
    static let edit: IconVal = 0xf044
    static let eject: IconVal = 0xf052
    static let ellipsis_h: IconVal = 0xf141
    static let ellipsis_v: IconVal = 0xf142
    static let empire: IconVal = 0xf1d1
    static let envelope: IconVal = 0xf0e0
    static let envelope_o: IconVal = 0xf003
    static let envelope_square: IconVal = 0xf199
    static let eraser: IconVal = 0xf12d
    static let eur: IconVal = 0xf153
    static let euro: IconVal = 0xf153
    static let exchange: IconVal = 0xf0ec
    static let exclamation: IconVal = 0xf12a
    static let exclamation_circle: IconVal = 0xf06a
    static let exclamation_triangle: IconVal = 0xf071
    static let expand: IconVal = 0xf065
    static let external_link: IconVal = 0xf08e
    static let external_link_square: IconVal = 0xf14c
    static let eye: IconVal = 0xf06e
    static let eye_slash: IconVal = 0xf070
    static let eyedropper: IconVal = 0xf1fb
    static let facebook: IconVal = 0xf09a
    static let facebook_f: IconVal = 0xf09a
    static let facebook_official: IconVal = 0xf230
    static let facebook_square: IconVal = 0xf082
    static let fast_backward: IconVal = 0xf049
    static let fast_forward: IconVal = 0xf050
    static let fax: IconVal = 0xf1ac
    static let female: IconVal = 0xf182
    static let fighter_jet: IconVal = 0xf0fb
    static let file: IconVal = 0xf15b
    static let file_archive_o: IconVal = 0xf1c6
    static let file_audio_o: IconVal = 0xf1c7
    static let file_code_o: IconVal = 0xf1c9
    static let file_excel_o: IconVal = 0xf1c3
    static let file_image_o: IconVal = 0xf1c5
    static let file_movie_o: IconVal = 0xf1c8
    static let file_o: IconVal = 0xf016
    static let file_pdf_o: IconVal = 0xf1c1
    static let file_photo_o: IconVal = 0xf1c5
    static let file_picture_o: IconVal = 0xf1c5
    static let file_powerpoint_o: IconVal = 0xf1c4
    static let file_sound_o: IconVal = 0xf1c7
    static let file_text: IconVal = 0xf15c
    static let file_text_o: IconVal = 0xf0f6
    static let file_video_o: IconVal = 0xf1c8
    static let file_word_o: IconVal = 0xf1c2
    static let file_zip_o: IconVal = 0xf1c6
    static let files_o: IconVal = 0xf0c5
    static let film: IconVal = 0xf008
    static let filter: IconVal = 0xf0b0
    static let fire: IconVal = 0xf06d
    static let fire_extinguisher: IconVal = 0xf134
    static let flag: IconVal = 0xf024
    static let flag_checkered: IconVal = 0xf11e
    static let flag_o: IconVal = 0xf11d
    static let flash: IconVal = 0xf0e7
    static let flask: IconVal = 0xf0c3
    static let flickr: IconVal = 0xf16e
    static let floppy_o: IconVal = 0xf0c7
    static let folder: IconVal = 0xf07b
    static let folder_o: IconVal = 0xf114
    static let folder_open: IconVal = 0xf07c
    static let folder_open_o: IconVal = 0xf115
    static let font: IconVal = 0xf031
    static let forumbee: IconVal = 0xf211
    static let forward: IconVal = 0xf04e
    static let foursquare: IconVal = 0xf180
    static let frown_o: IconVal = 0xf119
    static let futbol_o: IconVal = 0xf1e3
    static let gamepad: IconVal = 0xf11b
    static let gavel: IconVal = 0xf0e3
    static let gbp: IconVal = 0xf154
    static let ge: IconVal = 0xf1d1
    static let gear: IconVal = 0xf013
    static let gears: IconVal = 0xf085
    static let genderless: IconVal = 0xf1db
    static let gift: IconVal = 0xf06b
    static let git: IconVal = 0xf1d3
    static let git_square: IconVal = 0xf1d2
    static let github: IconVal = 0xf09b
    static let github_alt: IconVal = 0xf113
    static let github_square: IconVal = 0xf092
    static let gittip: IconVal = 0xf184
    static let glass: IconVal = 0xf000
    static let globe: IconVal = 0xf0ac
    static let google: IconVal = 0xf1a0
    static let google_plus: IconVal = 0xf0d5
    static let google_plus_square: IconVal = 0xf0d4
    static let google_wallet: IconVal = 0xf1ee
    static let graduation_cap: IconVal = 0xf19d
    static let gratipay: IconVal = 0xf184
    static let group: IconVal = 0xf0c0
    static let h_square: IconVal = 0xf0fd
    static let hacker_news: IconVal = 0xf1d4
    static let hand_o_down: IconVal = 0xf0a7
    static let hand_o_left: IconVal = 0xf0a5
    static let hand_o_right: IconVal = 0xf0a4
    static let hand_o_up: IconVal = 0xf0a6
    static let hdd_o: IconVal = 0xf0a0
    static let header: IconVal = 0xf1dc
    static let headphones: IconVal = 0xf025
    static let heart: IconVal = 0xf004
    static let heart_o: IconVal = 0xf08a
    static let heartbeat: IconVal = 0xf21e
    static let history: IconVal = 0xf1da
    static let home: IconVal = 0xf015
    static let hospital_o: IconVal = 0xf0f8
    static let hotel: IconVal = 0xf236
    static let html5: IconVal = 0xf13b
    static let ils: IconVal = 0xf20b
    static let image: IconVal = 0xf03e
    static let inbox: IconVal = 0xf01c
    static let indent: IconVal = 0xf03c
    static let info: IconVal = 0xf129
    static let info_circle: IconVal = 0xf05a
    static let inr: IconVal = 0xf156
    static let instagram: IconVal = 0xf16d
    static let institution: IconVal = 0xf19c
    static let ioxhost: IconVal = 0xf208
    static let italic: IconVal = 0xf033
    static let joomla: IconVal = 0xf1aa
    static let jpy: IconVal = 0xf157
    static let jsfiddle: IconVal = 0xf1cc
    static let key: IconVal = 0xf084
    static let keyboard_o: IconVal = 0xf11c
    static let krw: IconVal = 0xf159
    static let language: IconVal = 0xf1ab
    static let laptop: IconVal = 0xf109
    static let lastfm: IconVal = 0xf202
    static let lastfm_square: IconVal = 0xf203
    static let leaf: IconVal = 0xf06c
    static let leanpub: IconVal = 0xf212
    static let legal: IconVal = 0xf0e3
    static let lemon_o: IconVal = 0xf094
    static let level_down: IconVal = 0xf149
    static let level_up: IconVal = 0xf148
    static let life_bouy: IconVal = 0xf1cd
    static let life_buoy: IconVal = 0xf1cd
    static let life_ring: IconVal = 0xf1cd
    static let life_saver: IconVal = 0xf1cd
    static let lightbulb_o: IconVal = 0xf0eb
    static let line_chart: IconVal = 0xf201
    static let link: IconVal = 0xf0c1
    static let linkedin: IconVal = 0xf0e1
    static let linkedin_square: IconVal = 0xf08c
    static let linux: IconVal = 0xf17c
    static let list: IconVal = 0xf03a
    static let list_alt: IconVal = 0xf022
    static let list_ol: IconVal = 0xf0cb
    static let list_ul: IconVal = 0xf0ca
    static let location_arrow: IconVal = 0xf124
    static let lock: IconVal = 0xf023
    static let long_arrow_down: IconVal = 0xf175
    static let long_arrow_left: IconVal = 0xf177
    static let long_arrow_right: IconVal = 0xf178
    static let long_arrow_up: IconVal = 0xf176
    static let magic: IconVal = 0xf0d0
    static let magnet: IconVal = 0xf076
    static let mail_forward: IconVal = 0xf064
    static let mail_reply: IconVal = 0xf112
    static let mail_reply_all: IconVal = 0xf122
    static let male: IconVal = 0xf183
    static let map_marker: IconVal = 0xf041
    static let mars: IconVal = 0xf222
    static let mars_double: IconVal = 0xf227
    static let mars_stroke: IconVal = 0xf229
    static let mars_stroke_h: IconVal = 0xf22b
    static let mars_stroke_v: IconVal = 0xf22a
    static let maxcdn: IconVal = 0xf136
    static let meanpath: IconVal = 0xf20c
    static let medium: IconVal = 0xf23a
    static let medkit: IconVal = 0xf0fa
    static let meh_o: IconVal = 0xf11a
    static let mercury: IconVal = 0xf223
    static let microphone: IconVal = 0xf130
    static let microphone_slash: IconVal = 0xf131
    static let minus: IconVal = 0xf068
    static let minus_circle: IconVal = 0xf056
    static let minus_square: IconVal = 0xf146
    static let minus_square_o: IconVal = 0xf147
    static let mobile: IconVal = 0xf10b
    static let mobile_phone: IconVal = 0xf10b
    static let money: IconVal = 0xf0d6
    static let moon_o: IconVal = 0xf186
    static let mortar_board: IconVal = 0xf19d
    static let motorcycle: IconVal = 0xf21c
    static let music: IconVal = 0xf001
    static let naviconVal: IconVal = 0xf0c9
    static let neuter: IconVal = 0xf22c
    static let newspaper_o: IconVal = 0xf1ea
    static let openid: IconVal = 0xf19b
    static let outdent: IconVal = 0xf03b
    static let pagelines: IconVal = 0xf18c
    static let paint_brush: IconVal = 0xf1fc
    static let paper_plane: IconVal = 0xf1d8
    static let paper_plane_o: IconVal = 0xf1d9
    static let paperclip: IconVal = 0xf0c6
    static let paragraph: IconVal = 0xf1dd
    static let paste: IconVal = 0xf0ea
    static let pause: IconVal = 0xf04c
    static let paw: IconVal = 0xf1b0
    static let paypal: IconVal = 0xf1ed
    static let pencil: IconVal = 0xf040
    static let pencil_square: IconVal = 0xf14b
    static let pencil_square_o: IconVal = 0xf044
    static let phone: IconVal = 0xf095
    static let phone_square: IconVal = 0xf098
    static let photo: IconVal = 0xf03e
    static let picture_o: IconVal = 0xf03e
    static let pie_chart: IconVal = 0xf200
    static let pied_piper: IconVal = 0xf1a7
    static let pied_piper_alt: IconVal = 0xf1a8
    static let pinterest: IconVal = 0xf0d2
    static let pinterest_p: IconVal = 0xf231
    static let pinterest_square: IconVal = 0xf0d3
    static let plane: IconVal = 0xf072
    static let play: IconVal = 0xf04b
    static let play_circle: IconVal = 0xf144
    static let play_circle_o: IconVal = 0xf01d
    static let plug: IconVal = 0xf1e6
    static let plus: IconVal = 0xf067
    static let plus_circle: IconVal = 0xf055
    static let plus_square: IconVal = 0xf0fe
    static let plus_square_o: IconVal = 0xf196
    static let power_off: IconVal = 0xf011
    static let print: IconVal = 0xf02f
    static let puzzle_piece: IconVal = 0xf12e
    static let qq: IconVal = 0xf1d6
    static let qrcode: IconVal = 0xf029
    static let question: IconVal = 0xf128
    static let question_circle: IconVal = 0xf059
    static let quote_left: IconVal = 0xf10d
    static let quote_right: IconVal = 0xf10e
    static let ra: IconVal = 0xf1d0
    static let random: IconVal = 0xf074
    static let rebel: IconVal = 0xf1d0
    static let recycle: IconVal = 0xf1b8
    static let reddit: IconVal = 0xf1a1
    static let reddit_square: IconVal = 0xf1a2
    static let refresh: IconVal = 0xf021
    static let remove: IconVal = 0xf00d
    static let renren: IconVal = 0xf18b
    static let reorder: IconVal = 0xf0c9
    static let repeat: IconVal = 0xf01e
    static let reply: IconVal = 0xf112
    static let reply_all: IconVal = 0xf122
    static let retweet: IconVal = 0xf079
    static let rmb: IconVal = 0xf157
    static let road: IconVal = 0xf018
    static let rocket: IconVal = 0xf135
    static let rotate_left: IconVal = 0xf0e2
    static let rotate_right: IconVal = 0xf01e
    static let rouble: IconVal = 0xf158
    static let rss: IconVal = 0xf09e
    static let rss_square: IconVal = 0xf143
    static let rub: IconVal = 0xf158
    static let ruble: IconVal = 0xf158
    static let rupee: IconVal = 0xf156
    static let save: IconVal = 0xf0c7
    static let scissors: IconVal = 0xf0c4
    static let search: IconVal = 0xf002
    static let search_minus: IconVal = 0xf010
    static let search_plus: IconVal = 0xf00e
    static let sellsy: IconVal = 0xf213
    static let send: IconVal = 0xf1d8
    static let send_o: IconVal = 0xf1d9
    static let server: IconVal = 0xf233
    static let share: IconVal = 0xf064
    static let share_alt: IconVal = 0xf1e0
    static let share_alt_square: IconVal = 0xf1e1
    static let share_square: IconVal = 0xf14d
    static let share_square_o: IconVal = 0xf045
    static let shekel: IconVal = 0xf20b
    static let sheqel: IconVal = 0xf20b
    static let shield: IconVal = 0xf132
    static let ship: IconVal = 0xf21a
    static let shirtsinbulk: IconVal = 0xf214
    static let shopping_cart: IconVal = 0xf07a
    static let sign_in: IconVal = 0xf090
    static let sign_out: IconVal = 0xf08b
    static let signal: IconVal = 0xf012
    static let simplybuilt: IconVal = 0xf215
    static let sitemap: IconVal = 0xf0e8
    static let skyatlas: IconVal = 0xf216
    static let skype: IconVal = 0xf17e
    static let slack: IconVal = 0xf198
    static let sliders: IconVal = 0xf1de
    static let slideshare: IconVal = 0xf1e7
    static let smile_o: IconVal = 0xf118
    static let soccer_ball_o: IconVal = 0xf1e3
    static let sort: IconVal = 0xf0dc
    static let sort_alpha_asc: IconVal = 0xf15d
    static let sort_alpha_desc: IconVal = 0xf15e
    static let sort_amount_asc: IconVal = 0xf160
    static let sort_amount_desc: IconVal = 0xf161
    static let sort_asc: IconVal = 0xf0de
    static let sort_desc: IconVal = 0xf0dd
    static let sort_down: IconVal = 0xf0dd
    static let sort_numeric_asc: IconVal = 0xf162
    static let sort_numeric_desc: IconVal = 0xf163
    static let sort_up: IconVal = 0xf0de
    static let soundcloud: IconVal = 0xf1be
    static let space_shuttle: IconVal = 0xf197
    static let spinner: IconVal = 0xf110
    static let spoon: IconVal = 0xf1b1
    static let spotify: IconVal = 0xf1bc
    static let square: IconVal = 0xf0c8
    static let square_o: IconVal = 0xf096
    static let stack_exchange: IconVal = 0xf18d
    static let stack_overflow: IconVal = 0xf16c
    static let star: IconVal = 0xf005
    static let star_half: IconVal = 0xf089
    static let star_half_empty: IconVal = 0xf123
    static let star_half_full: IconVal = 0xf123
    static let star_half_o: IconVal = 0xf123
    static let star_o: IconVal = 0xf006
    static let steam: IconVal = 0xf1b6
    static let steam_square: IconVal = 0xf1b7
    static let step_backward: IconVal = 0xf048
    static let step_forward: IconVal = 0xf051
    static let stethoscope: IconVal = 0xf0f1
    static let stop: IconVal = 0xf04d
    static let street_view: IconVal = 0xf21d
    static let strikethrough: IconVal = 0xf0cc
    static let stumbleupon: IconVal = 0xf1a4
    static let stumbleupon_circle: IconVal = 0xf1a3
    static let sub_script: IconVal = 0xf12c
    static let subway: IconVal = 0xf239
    static let suitstatic: IconVal = 0xf0f2
    static let sun_o: IconVal = 0xf185
    static let super_script: IconVal = 0xf12b
    static let support: IconVal = 0xf1cd
    static let table: IconVal = 0xf0ce
    static let tablet: IconVal = 0xf10a
    static let tachometer: IconVal = 0xf0e4
    static let tag: IconVal = 0xf02b
    static let tags: IconVal = 0xf02c
    static let tasks: IconVal = 0xf0ae
    static let taxi: IconVal = 0xf1ba
    static let tencent_weibo: IconVal = 0xf1d5
    static let terminal: IconVal = 0xf120
    static let text_height: IconVal = 0xf034
    static let text_width: IconVal = 0xf035
    static let th: IconVal = 0xf00a
    static let th_large: IconVal = 0xf009
    static let th_list: IconVal = 0xf00b
    static let thumb_tack: IconVal = 0xf08d
    static let thumbs_down: IconVal = 0xf165
    static let thumbs_o_down: IconVal = 0xf088
    static let thumbs_o_up: IconVal = 0xf087
    static let thumbs_up: IconVal = 0xf164
    static let ticket: IconVal = 0xf145
    static let times: IconVal = 0xf00d
    static let times_circle: IconVal = 0xf057
    static let times_circle_o: IconVal = 0xf05c
    static let tint: IconVal = 0xf043
    static let toggle_down: IconVal = 0xf150
    static let toggle_left: IconVal = 0xf191
    static let toggle_off: IconVal = 0xf204
    static let toggle_on: IconVal = 0xf205
    static let toggle_right: IconVal = 0xf152
    static let toggle_up: IconVal = 0xf151
    static let train: IconVal = 0xf238
    static let transgender: IconVal = 0xf224
    static let transgender_alt: IconVal = 0xf225
    static let trash: IconVal = 0xf1f8
    static let trash_o: IconVal = 0xf014
    static let tree: IconVal = 0xf1bb
    static let trello: IconVal = 0xf181
    static let trophy: IconVal = 0xf091
    static let truck: IconVal = 0xf0d1
    static let try: IconVal = 0xf195
    static let tty: IconVal = 0xf1e4
    static let tumblr: IconVal = 0xf173
    static let tumblr_square: IconVal = 0xf174
    static let turkish_lira: IconVal = 0xf195
    static let twitch: IconVal = 0xf1e8
    static let twitter: IconVal = 0xf099
    static let twitter_square: IconVal = 0xf081
    static let umbrella: IconVal = 0xf0e9
    static let underline: IconVal = 0xf0cd
    static let undo: IconVal = 0xf0e2
    static let university: IconVal = 0xf19c
    static let unlink: IconVal = 0xf127
    static let unlock: IconVal = 0xf09c
    static let unlock_alt: IconVal = 0xf13e
    static let unsorted: IconVal = 0xf0dc
    static let upload: IconVal = 0xf093
    static let usd: IconVal = 0xf155
    static let user: IconVal = 0xf007
    static let user_md: IconVal = 0xf0f0
    static let user_plus: IconVal = 0xf234
    static let user_secret: IconVal = 0xf21b
    static let user_times: IconVal = 0xf235
    static let users: IconVal = 0xf0c0
    static let venus: IconVal = 0xf221
    static let venus_double: IconVal = 0xf226
    static let venus_mars: IconVal = 0xf228
    static let viacoin: IconVal = 0xf237
    static let video_camera: IconVal = 0xf03d
    static let vimeo_square: IconVal = 0xf194
    static let vine: IconVal = 0xf1ca
    static let vk: IconVal = 0xf189
    static let volume_down: IconVal = 0xf027
    static let volume_off: IconVal = 0xf026
    static let volume_up: IconVal = 0xf028
    static let warning: IconVal = 0xf071
    static let wechat: IconVal = 0xf1d7
    static let weibo: IconVal = 0xf18a
    static let weixin: IconVal = 0xf1d7
    static let whatsapp: IconVal = 0xf232
    static let wheelchair: IconVal = 0xf193
    static let wifi: IconVal = 0xf1eb
    static let windows: IconVal = 0xf17a
    static let won: IconVal = 0xf159
    static let wordpress: IconVal = 0xf19a
    static let wrench: IconVal = 0xf0ad
    static let xing: IconVal = 0xf168
    static let xing_square: IconVal = 0xf169
    static let yahoo: IconVal = 0xf19e
    static let yelp: IconVal = 0xf1e9
    static let yen: IconVal = 0xf157
    static let youtube: IconVal = 0xf167
    static let youtube_play: IconVal = 0xf16a
    static let youtube_square: IconVal = 0xf166
}