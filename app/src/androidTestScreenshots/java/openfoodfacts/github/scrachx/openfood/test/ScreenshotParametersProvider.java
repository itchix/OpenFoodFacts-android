package openfoodfacts.github.scrachx.openfood.test;

import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ScreenshotParametersProvider {
    public static ScreenshotParameter create(String countryTag, String languageCode, String... otherProductsCode) {
        return create(countryTag, LocaleHelper.getLocale(languageCode), otherProductsCode);
    }

    private static ScreenshotParameter create(String countryTag, Locale locale, String... otherProductsCode) {
        ScreenshotParameter parameter = new ScreenshotParameter(countryTag, locale);
        parameter.setProductCodes(Arrays.asList(otherProductsCode));
        return parameter;
    }

    public static List<ScreenshotParameter> createDefault() {
        List<ScreenshotParameter> res = new ArrayList<>();
//        res.add(create("Albania", new Locale("sq", "AL"),"8076802795019","9006900014872","3250390003755","90087745","8600116000073","8003340069708","8600116000301","8600946862018","9120025838981","5060517885830","9006900014360","8002560070709","8000350004583"));
//        res.add(create("Algeria", new Locale("ar", "DZ"),"80177616","5449000000439","5449000000996","0065633071308","3450970006460","8000500037560","6191544100609","3564707098823","3564700759349","3564700676257","3564700555347","5410126716016","6130234002168","6133320000130","6130517000430","6130093056968","6130837001186","6130837001285","6130234000607","6130760005152"));
//        res.add(create("Andorra", new Locale("ca", "AD"),"3770002675113","8410100034027","3450970150828","8437000140481"));
//        res.add(create("Anguilla", new Locale("en", "AI"),"0041303014912"));
//        res.add(create("Argentina", new Locale("es", "AR"),"3017620429484","7792129002326","7792129002319","7791273000707","7791875005353","7796425000048","7792260000823"));
//        res.add(create("Australia", new Locale("en", "AU"),"26201142","26201542","9310881980140","9556092706166","9315070606634","9310036005988","9353712000037","9315896100316","9300462118437","9348360000003","8032595021407","9313107160265","9300682038911","22012401","9300639151328","26222035","9300601930203","9300796079947","9300645069013","9300601183395"));
//        res.add(create("Austria", new Locale("de", "AT"),"8000430900231","8712566328352","4000417702005","8076800195057","8076808060654","90162800","3083680073608","9015160102243","24045476","20763961","3838929513045","9100000051598","9100000716695","5449000000439","5760466920537","40144177","5449000000996","5900951028502","9100000733845","4008400191423"));
//        res.add(create("Bahrein", new Locale("ar", "BH"),"4025500183677"));
//        res.add(create("Belgium", new Locale("nl", "BE"),"8000430900231","8010721001509","80551072","5412929001092","5400101067554","5410483015036","5053990107339","5412038129083","5411021100047","20803902","3387390326574","8712566328352","5400601350835","4000417702005","20047214","20363253","5400112717127","20427986","87157239","5400141189841"));
//        res.add(create("Bosnia and Herzegovina", new Locale("bs", "BA"),"7622210642189","7622210642134","7622210642196","9006900014872","5900437005133","8606014409031","5941047829214","3850108020182","8600116000073","8600498176359","8600939212257","5204739612649","3831051008700","3875000050808","7622210397119","8600116000301","9120025838981","9006900014360","8600939212608","8607100510433"));
//        res.add(create("Brazil", new Locale("pt", "BR"),"5449000000996","8410770100022","7891000100448","3700123302360","7896984205004","7891000140307","7898941911065","7895000371303","7898176581187","7891991000826","5601045300022","3014680009175","7896336006624","3525","3760091729828","7896000530363","7896004002767","7894321722016","7896496940325","7891098000170"));
//        res.add(create("Bulgaria", new Locale("bg", "BG"),"7613036230506","4014051006092","8412779400103","4008400290126","3800055710063","4014400911039","7622210633354","7622210642189","5900259056207","4260012620276","7622210642134","7622210642196","8480017743398","2000228316639","9006900014872","5900437005133","8430807002297","8430807006240","5941000024700","4823077614767"));
//        res.add(create("Burkina Faso", new Locale("fr", "BF"),"9504000105013","8728200404009","3258561100104"));
//        res.add(create("Cambodia", new Locale("km", "KH"),"5410041332209","9310113532277","8851234310173"));
//        res.add(create("Cameroon", new Locale("fr", "CM"),"5449000000996","80177173","9501100001627","0853148003002","8425197712024","6174000001566","0853148003019","6001240100035"));
//        res.add(create("Canada", new Locale("en", "CA"),"0065633074712","0063667125080","26035277","0056800528022","0055220080059","26043357","26020457","0016000275270","0063348004949","26039886","26073835","26073347","26043579","26044439","26015057","26043081","26044064","3168930009993","5449000000996","0619893610557"));
//        res.add(create("Chile", new Locale("es", "CL"),"7801505000877","4003274001625","8410134013906","7502252481796","24500200","7801552000196","7804647172938","7802900365028","7802600133217","0832697000595"));
//        res.add(create("China", new Locale("zh-hans", "CN"),"8422174010029","3228021000084","3083680004671","4008400191423","4045317058593","3272770003148","5449000017932","3760150148607","80177173","0898999000022","8851978808158","6954767417684","8005709800984","6936571910018","6901939691601","4000417294005"));
//        res.add(create("Colombia", new Locale("es", "CO"),"7709335802602","3017620429484","1016001015256","3222471055113","3525","3248650098566","3350031544545","7702189109996","7702011012401","7707220540370","2147729071251","7702729934323","7702011281180"));
//        res.add(create("Cook Islands", new Locale("en", "CK"),"94155877"));
//        res.add(create("Costa Rica", new Locale("es", "CR"),"0658480001248","4008713702750","0747627005116","0873617002671","7000000143052"));
//        res.add(create("Côte d'Ivoire", new Locale("fr", "CI"),"3700311866292","8716200718585","6290090014559","3168930006725","3428271940066"));
//        res.add(create("Croatia", new Locale("hr", "HR"),"90426230","7622210642189","8076800376999","8023678162360","4260012620276","7622210642134","7622210642196","9006900014872","8714882001018","7622210982377","20641788","8606014409031","3859889622356","5941047829214","5997347555735","8600116000073","8600939212257","5204739612649","3831051008700","7622210254405"));
//        res.add(create("Cuba", new Locale("es", "CU"),"0037000184799","2000000044046","2000000044055","2000000044054","8500001030249"));
//        res.add(create("Cyprus", new Locale("el", "CY"),"5000168181578","5201168215649","8690504025207","8606003341137"));
//        res.add(create("Czech Republic", new Locale("cs", "CZ"),"4000417702005","90426230","5449000000439","20221133","5026489484785","5449000004864","4001686834022","8712566065011","8594001040018","8593893742840","5449000131843","20706852","5449000000286","4260012620276","7622210835338","4046234925906","8594002648008","85815483","4001954165513","8594001021499"));
//        res.add(create("Democratic Republic of the Congo (Congo-Kinshasa, former Zaire)", new Locale("fr", "CD"),"3258561410784"));
        res.add(create("Denmark", new Locale("da", "DK"),"8000430900231","8712566328352","4000417702005","87157239","7622300336738","3046920028363","3228021000084","5000159407236","8410770100022","0024000011859","5000111018005","7613032625474","8010721998120","3395320066797","4000417214003","4014400400007","8410000810004","7622210477439","80177173","8713800132131"));
//        res.add(create("Djibouti", new Locale("fr", "DJ"),"3183280000902","3263852506411"));
//        res.add(create("Dominica", new Locale("en", "DM"),"0888670002919"));
//        res.add(create("Dominican Republic", new Locale("es", "DO"),"0065633438699","3451790886645"));
//        res.add(create("Ecuador", new Locale("es", "EC"),"7861006744045","7861077501592"));
//        res.add(create("Egypt", new Locale("ar", "EG"),"90162602","5000159031103","9557062321136","8714555000300","6221024991714","6223001360506"));
//        res.add(create("El Salvador", new Locale("es", "SV"),"7317731117352","2319374130793"));
//        res.add(create("Estonia", new Locale("et", "EE"),"2000228316639","80052760","3800020423578","5900437005133","40561073","8710908980459","7613035789197","4000415028701","4000607854606","6415717409000","4000415025304","6416453554719","7613033963391","90384103","90384066","5900020027689","9020200006917"));
//        res.add(create("Fiji", new Locale("en", "FJ"),"9403146721883"));
//        res.add(create("Finland", new Locale("fi", "FI"),"4000417289001","20036850","20022464","5900334007919","7310500094465","6430043013219","6408430011667","6411401028373","20425609","8713800132131","20366780","6415712002718","6415715930988","6417348171608","5410041001204","8008698007303","6430031984071","7622300571351","6415712508593","3770006832055"));
        res.add(create("France", new Locale("fr", "FR"),"3760224570167","3180950004802","5053827101424","3180950006981","3240930313505","3180950001801","3154230010197","3073780977524","8000430900231","3449865293375","3073780516723","3181232180559","3180950007254","3180950007308","3073780515702","3245390181022","3180950008114","3235080008654","3273220086230","5055534325117"));
//        res.add(create("French Guiana", new Locale("fr", "GF"),"3256220258579","3256221653922","3256223010563","3256223022399","3256224234555","3017800132463","3308751000216","3229820780955","3263852557215","5741000002315","3487400000415","3019080031009","3435649826570","3760096960547","3256220659147","3244570054705","3256220280174","3256220280167","3368952621207","3256225721603"));
//        res.add(create("French Polynesia", new Locale("fr", "PF"),"0613008730734","0088009670721","3352271322211","9415262002104","3352271259227","80176800","15600703","3352271120060","9421903084576","3760251180094","0088009320633","3057640373183","3760150148607","3760150142933","9415102000857","3700279301491","3017760826174","3256225721603","3256225427239","3256222074573"));
//        res.add(create("Gabon", new Locale("fr", "GA"),"3052011006318","8435185100955","3410280024318"));
//        res.add(create("Georgia", new Locale("ka", "GE"),"5449000000996","54491472"));
        res.add(create("Germany", new Locale("de", "DE"),"8000430900231","5055534325117","4008258143018","8010721001509","2000000095221","4008258100011","4032844250835","7610800005926","20490607","4388840213023","23625983","4002971453409","4000856007129","8712566328352","4000417289001","7622300479008","4000417702005","20001308","20095291","87157239"));
//        res.add(create("Greece", new Locale("el", "GR"),"4000417289001","5034525010119","5000159509831","3560070614202","8076800376999","3560070759927","7622300315269","8076808150072","20181529","5201004021328","3560070046478","9006900014872","20641788","59032823","8410376015515","5000159366229","5201024781288","4000339695027","5203937007721","7622300507121"));
//        res.add(create("Guadeloupe", new Locale("fr", "GP"),"3270190128519","7610036002676","80176800","3056440083193","3368850000036","3770000495010","3346438000104","3575650000207","15600703","3176571983008","3440950200476","3270190024972","3245411851149","3245412950872","3018930004903","7613034916891","3328382631320","3292090141221","3227061000023","3029330031987"));
//        res.add(create("Guatemala", new Locale("es", "GT"),"7501055352463","7441001606069"));
//        res.add(create("Guyana", new Locale("en", "GY"),"0024000163022"));
//        res.add(create("Hong Kong (SAR of China)", new Locale("zh-hant", "HK"),"3472860053057","4891028164456","5010029000023","4897042280424","80177609","5901414204679","3222475425363","4890008120307","00847049","00854252","3274080001005","7610400014571","3222472627630","4891028710660","4892214253527","3222472777526","8410973012115","3271820071557","0078895132908","4897005440056"));
//        res.add(create("Hungary", new Locale("hu", "HU"),"8712566328352","20280239","4008400290126","90426230","20002077","3046920028363","5904215138747","4001686834022","5998339749729","54491472","8714100581810","80176800","5000184321552","5900102013067","5997380351080","7622210145390","8714100637487","4006814001529","8076800376999","7622210835338"));
//        res.add(create("Iceland", new Locale("is", "IS"),"5411188110743","8410076481597","5690576271918","5690527673006","5010029222173"));
//        res.add(create("India", new Locale("hi", "IN"),"5449000000996","87157246","3256226759117","4060800001740","7610827760259","3124480184320","8901491208291","8901491990028","8901491990080","8904150502396","8902080011513","8901491365703","4000417224002","8901071706834","8901491102902","8901499008190","8904787201723","8901499008244","8901030691508","8904065212540"));
//        res.add(create("Indonesia", new Locale("id", "ID"),"8999999002503","13551128"));
//        res.add(create("Iran", new Locale("fa", "IR"),"22123572"));
//        res.add(create("Iraq", new Locale("ar", "IQ"),"5449000000439","5449000130389"));
//        res.add(create("Ireland", new Locale("en", "IE"),"5060088701690","8712566328352","90162800","5057753210823","7622210256225","20471613","4260334140148","20152543","5900951028502","3051800802735","5099874167020","5036829752207","5018374350930","5013683305800","5060072990017","80176800","5000159500371","5060088705988","5053526008291","25179750"));
        res.add(create("Israel", new Locale("he", "IL"),"3525","5420066330234","0830296000404","8723400782100","7290002013839","0038527591053","7296073273424","8690777206051","3523230039437","7290106654945"));
        res.add(create("Italy", new Locale("it", "IT"),"3560071005672","8712566328352","3560070709366","80019442","8076809575508","8013355000290","8002670500714","8076809521543","3560070470501","3046920029759","7622300336738","4000540003864","8076802085738","8034066307461","20590147","8013355999143","8001120895530","8019730075877","8019730075440","8010721996812"));
//        res.add(create("Japan", new Locale("ja", "JP"),"8076802085738","3019080067015","3263670033458","3165950108019","3368952992758","3263670297812","8076800195019","8712566432127","3038352877008","3019081236250","8076809570657","3271510000126","3165950210651","3263670237719","3019081239640","3263670014457","3083680996082","5010029000023","3263670123616","3165950217797"));
//        res.add(create("Jordan", new Locale("ar", "JO"),"6290400013425","6251079000901","3560070894550","8423352107036"));
//        res.add(create("Kuweit", new Locale("ar", "KW"),"0037466080864","5449000014535"));
//        res.add(create("Latvia", new Locale("lv", "LV"),"2000228316639","4750127300601","80052760","3800020423578","5900437005133","40561073","7613035789197","4000415028701","4000607854606","6415717409000","4000415025304","4750127300694","7613033963391","90384103","90384066","5902978008307","5900020027689"));
//        res.add(create("Lebanon", new Locale("ar", "LB"),"0735143083626","5410677110752","0735143009145","6291003203527","3596710081738","0735143003044"));
//        res.add(create("Liechtenstein", new Locale("de", "LI"),"7614200110211"));
//        res.add(create("Lithuania", new Locale("lt", "LT"),"1702881020007","2000228316639","80177609","80052760","3800020423578","5900437005133","40561073","20636838","5900278011058","7613035789197","4000415028701","4000607854606","6415717409000","4000415025304","7613033963391","90384103","90384066","4770959440962","5900020027689"));
//        res.add(create("Luxembourg", new Locale("lb", "LU"),"8076802085981","5450168551633","4006040329039","3229820167398","5449000000996","54491472","4104420209336","7622210995063","3165440007808","4260489091043","3254568020230","5400134350104","3274080005003","8003892007432","8008698002100","8003655122501","5410063004238","3228022150078","4011100005846","5449000265104"));
//        res.add(create("Macedonia (Former Yugoslav Republic of)", new Locale("mk", "MK"),"7622210642189","7622210642134","7622210642196","8606014409031","3850108020182","8600116000073","8600939212257","5204739612649","7622210397119","8600116000301","8600946862018","9120025838981","8600939212608","8600043001105","8600043022469","8606014409093"));
//        res.add(create("Madagascar", new Locale("mg", "MG"),"9501046019205","3700005003064","3700005002357","3263852669116","6901209197789","3760153580909"));
//        res.add(create("Malaysia", new Locale("ms", "MY"),"9315090202014","9316434288602","9556587102053","0024000163015","9555653100368","8888140030734","9557062321136","9316434288107","9556570312131"));
//        res.add(create("Mali", new Locale("fr", "ML"),"5449000000996","3268840001008","3274080005003","3017624047813","5775345138880","3057640229138"));
//        res.add(create("Malta", new Locale("mt", "MT"),"20471613","8000500310427","5000159451666","7622300632762","5359902778858"));
//        res.add(create("Martinique", new Locale("fr", "MQ"),"3182180012183","3368959945573","5010477348678","5449000000996","80176800","15600703","3274080005003","3256225042364","6111069000499","3263857004615","3329120001054","3017760826174","3256225721603","3256225428762","3256221664010","3256222254203","3330261040604","3392590810938","3533630086542","3415581319149"));
//        res.add(create("Mauritius", new Locale("mfe", "MU"),"3046920029759","5053990119004","05428212","4770149208129","3258561140292","8437011503077"));
        res.add(create("Mexico", new Locale("es", "MX"),"7503018034058","7501000106301","7501055909582","7501295600621","7501020553154","7501020553147","7501055900077","04842293","7501055900060","7501058611857","7501024580217","7501020543032","7501295600713","7501791663663","7502252481789","7501032399238","8412224040175","8410069014276","8411555100152","0031200610355"));
//        res.add(create("Moldova", new Locale("ro", "MD"),"4008400290126","5900259056207","5941047814005","5941047829214","5942289000751","5941000024700","4840095009238","4840095001805","4841658000396","4841658000327","8000500119792"));
//        res.add(create("Monaco", new Locale("fr", "MC"),"8411317301285","3256225721603","8000643000384","3155250003312","3324498000746","3111902100051","3661405006973","3560070847945","3017760404396","3478220003519"));
//        res.add(create("Montenegro", new Locale("srp", "ME"),"8076800376999","8606014409048","8600102164802","40111216","9006900014872","54491069","8606014409031","8600116000073","8600498176359","5204739612649","9004380079602","7622210254405","5449000214775","8600116000301","8607100572691","8600939550076","8606018730469","8606104925991","4018077629334","8606107544144"));
//        res.add(create("Morocco", new Locale("fr", "MA"),"6111254878414","26035277","5449000000996","6111035000058","6111249941321","8411547001085","8410376036169","8000500037560","6111242923577","4017100263101","6111251420166","8410000810004","6111203002242","3017620401473","6111242101142","6111242104198","6111262580149","5410041001204","6111021090049","3229820794754"));
//        res.add(create("New Caledonia", new Locale("fr", "NC"),"5449000000996","9421903084293","3222473667215","9310055536623","3161911364531","3073780972703","3245414620131","87157246","9310072001777","3263851927019","3263859392017","3297760097143","3523230027946","3292590874094","3222471973530","3760020506698","3266191080994","3266191008035","3263850764417","3222474768430"));
//        res.add(create("New Zealand", new Locale("mi", "NZ"),"9300633340049","9300633443337","9300633919061","9300633905118","9300633953966","9400597019620","3760150148607","3760150142933","9310072001777","8076802085851","9400547007837","9556041780346","9415748021346","9311627010183","9310055536395","8714100897935","9310072021584","9556041611121","9415472101109","0637480010580"));
//        res.add(create("Niger", new Locale("fr", "NE"),"5290074003198"));
//        res.add(create("Norway", new Locale("nb", "NO"),"4000417702005","7622210477439","8713800132131","5021554989172","7041910057282","8008698007303","8076809545440","5021554989196","7040110642106","8714882001018","4000417269003","4251097402659","7622210626028","8410076481597","0200024009939","5712840020012","7035620024979","7039010016322","4001724017547","7312787740233"));
//        res.add(create("Oman", new Locale("ar", "OM"),"0032894010919"));
//        res.add(create("Pakistan", new Locale("en", "PK"),"5449000051981"));
//        res.add(create("Peru", new Locale("es", "PE"),"2644"));
//        res.add(create("Philippines", new Locale("en", "PH"),"0750515018402","0750515021228"));
        res.add(create("Poland", new Locale("pl", "PL"),"8000430900231","5900531003202","5900531003400","90426230","5902581687593","5900354038221","5902180040102","5900977008779","20053963","5901588017938","5901588018409","5900500027796","5900500024337","5900334012869","5900334006363","5900334013316","5902581687609","5941021000639","5904215138747","5900102005550"));
        res.add(create("Portugal", new Locale("pt", "PT"),"8000430900231","8076802085981","5601002009012","3046920028363","3596710354986","8410069014894","40052403","5449000000996","8410770100022","5600445608530","8436048414721","5601009160068","20177201","3250391997688","5601151543313","8722700462958","5601312047872","20413422","5601151213308","3250390221302"));
//        res.add(create("Puerto Rico", new Locale("es", "PR"),"7502270310320"));
//        res.add(create("Qatar", new Locale("ar", "QA"),"5000159031103"));
//        res.add(create("Republic of the Congo (Congo-Brazzaville)", new Locale("fr", "CG"),"6044000007250"));
//        res.add(create("Reunion", new Locale("fr", "RE"),"3017760314190","8000500227848","5449000000439","3560070309238","3412290057447","5449000000996","8710438110197","3350033118072","8410707110452","80176800","5449000050205","0653884020192","8888196172211","5202425000374","3368954300940","3286011100312","3061830001237","3222471092187","3222476078872","3308751000216"));
//        res.add(create("Romania", new Locale("ro", "RO"),"20280239","4008400290126","90426230","5900951247897","3046920028363","3038354199603","5941021001674","5941021000639","5904215138747","7622210836687","4311596435982","40052403","4009176454958","8601900001047","5449000240156","5941006101566","5941868200087","7622210633354","5949034000820","7613034955098"));
        res.add(create("Russia", new Locale("ru", "RU"),"8000430900231","4000417702005","8076800195057","4600300075409","4000417933003","3596710309115","4810410062897","3046920028363","3256221111774","5900259056207","8023678162360","5000157024671","6411402976802","3076820002064","4600935000418","5901067455077","3017620429484","8008698007303","40084701","3256225042067"));
//        res.add(create("Saint Kitts and Nevis", new Locale("en", "KN"),"0041303014912"));
//        res.add(create("Saint Martin (French part)", new Locale("fr", "MF"),"3368954000147","3228020160093","3032222970264"));
//        res.add(create("Saint Pierre and Miquelon", new Locale("fr", "PM"),"3019080067015","0064100238220","3263851990419","3263851990518","3250390778851","0060410017982","3250390657330","06401612","3199240000752","3250390146063","3250390779087","3263851505118","3263850426018","3263851990310","3250390000518","3250390111054","3103220009413","3218930128009","3451791340399","3083013804"));
//        //res.add(create("San Marino", new Locale("it", "SM"),[]));
//        res.add(create("Saudi Arabia", new Locale("ar", "SA"),"5449000000996","40111445","5941192110335","3017620429484","8996001320839","5941047829214","4000415043100","4000415025304","9557062321136","9006900014360","0737666003167","8595229909286"));
//        res.add(create("Sénégal", new Locale("fr", "SN"),"5449000000996","3596710022694","3073780972703","8010817105319","8480017741349","3263820006004","3760091720207","3700328403114","3464130002759","3250547014115","3222471131626","3228024910366","8725000156992","6044000019024","5901882012493","8718226323071","6044000007250"));
//        res.add(create("Serbia", new Locale("sr", "RS"),"80177616","8076800195057","8000430386219","5000159459228","54491472","3800205875307","3800205875604","9011900139616","5203064007656","5906425121137","8076800376999","8606014409048","7622210642134","7622210642196","8600102164802","40111216","5400111050447","80761761","3046920028004","8606011898951"));
//        res.add(create("Seychelles", new Locale("fr", "SC"),"3174660032347"));
//        res.add(create("Singapore", new Locale("zh-hans", "SG"),"3760152700667","8888026870010","8888196451217","8885012290470","8888196456519","8888196454713","8888196185013","8888026432812","8850161160790","0016300168340","8888030019566","8888010102899","8881304288255","8850025060105","5000159461122","9310155100038","8888010101649","3222473958450","9315536220107","9556183960996"));
//        res.add(create("Slovakia", new Locale("sk", "SK"),"90426230","5449000000286","4260012620276","7622210835338","5900130015736","5902768862515","9006900014872","8714100659922","5902121000165","5902121024987","5900497330503","40561073","4030387755497","5900966009138","5900552029564","5902097251608","5997347543893","8586008109898","4335896444543","5900020000576"));
//        res.add(create("Slovenia", new Locale("sl", "SI"),"20906177","3838929513045","4001686834022","4099200130453","80176800","3830067210060","7610700946053","7622210642189","24140836","4014400400007","8076800376999","8023678162360","3838900946701","7622210642134","7622210642196","24060776","20906122","8008698007303","8004225047354","3830023481237"));
//        res.add(create("South Africa", new Locale("en", "ZA"),"8410076900418","6001052001018","3017620429484","6009188002213","5449000009067","6002870002164","6009198000452","6009188001216","6001704009614","6004923000516","6004052000463","6009880012381","6001299000270","6001032424646","6009900028224","6001068595808","6009900028200","6009510802542","6001068592401","5449000664761"));
//        //res.add(create("South Georgia and the South Sandwich Islands", new Locale("en", "GS"),[]));
        res.add(create("South Korea", new Locale("ko", "KR"),"3222475310829","8801043020756","3178530412567","2000000021345"));
        res.add(create("Spain", new Locale("ast", "ES"),"8480000561596","8413100612615","8480000826466","8410408050279","7613036230506","3560070696840","8422584314380","3000027364076","24087643","8429359002008","3700003781162","24021456","8480017142115","8410285114897","8422584315745","8480000109286","3770002675113","20095291","87157239","20452070"));
//        res.add(create("Swaziland", new Locale("en", "SZ"),"6009198001329","6009705211036","5449000006844"));
//        res.add(create("Sweden", new Locale("sv", "SE"),"8000430900231","8712566328352","4000417289001","4000417702005","20001308","87157239","5711953035104","6408432087868","7350011740680","3046920029759","20462369","8000430058666","20131968","8000430386219","5449000000439","7312080004025","20543075","9006900204099","20166083","3046920047302"));
        res.add(create("Switzerland", new Locale("de", "CH"),"8000430900231","3073780516723","3181232180559","3073780515702","4008258143018","8010721001509","7610809035856","8886303210238","7616700100358","2112767004707","7613034700810","4008258100011","7610800005926","3095758863011","3261055420503","3387390326574","3228886043714","7610200248503","8712566328352","7610845376609"));
//        res.add(create("Syria", new Locale("ar", "SY"),"9557062321136"));
//        res.add(create("Taiwan", new Locale("zh-hant", "TW"),"5010029000023","4710060010180","4710060010012","8996001320839","3560070749027","4710783055116","4710018000102","3461820210371","4712929110178","4710209334115","4710018146305","4710018149108","1490274741355","4710126040595","4710128020106","4710209705113","4719859741397","4710176039136","4710143930510","4710063337710"));
//        res.add(create("Thailand", new Locale("th", "TH"),"8850045171959","8851351383548","8851028004073","8850123110115","8858684502585","8850125073807","8859501320085","8850188270106","8859473100050","8851013748494","8852021300001","8858893917491","8858998581115","8850088601901","8850511221140","8858702410311","8856742000028","8850329145867","8850511221843","8852052110501"));
//        res.add(create("The Netherlands", new Locale("nl", "NL"),"8000430900231","5412929001092","8712566328352","20799892","3263670041255","40097138","5411188094159","1101803","8714685902086","3428420053203","5053827167666","20622411","3380380072413","3760052232299","5034525010119","4012359113108","5411188123446","8714100873885","8008698003213","20195090"));
//        res.add(create("Tunisia", new Locale("ar", "TN"),"6194002510316","8000500037560","6191544100609","6194019605258","6194029100415","11940016","6191513501031","6194001800111","6194043001255","6194002510064","6191507249635","6194003801895","5000159031103","6194005446100","4025700001023","3046920023009","6191507249505","6194005413058","6194007510014","3760113766480"));
//        res.add(create("Turkey", new Locale("tr", "TR"),"8690575064310","4012625419910","4000415043100","8690574102457","4000415025304","8690504025207","8690565002988","8690787311059","8690777564007"));
//        res.add(create("Ukraine", new Locale("uk", "UA"),"7622210659156","5901367001240","5901367001172","4823077614767","4840095009238","4840095001805","90384103","90384066"));
//        res.add(create("United Arab Emirates", new Locale("ar", "AE"),"5941192110335","8410076800442","7613036438100","8410707000197","5000159031103","5900189004491","6290400013425","0863769000229","5000127049307","0050700559124"));
        res.add(create("United Kingdom", new Locale("en", "GB"),"3760224570167","3180950004802","5053827101424","3180950006981","3240930313505","3180950001801","0011110807571","3154230010197","3073780977524","8000430900231","3449865293375","3073780516723","3181232180559","3180950007254","5050854766947","3180950007308","8480000561596","3073780515702","3245390181022","3180950008114"));
        res.add(create("United States of America", new Locale("en", "US"),"0011110807571","0694990008506","0812475012293","0013800188076","0061954004452","0083737250122","0052603065061","0084253222143","0061954000539","0816979010250","0058449870241","0786162110008","5053990119004","0024463061163","0016000275270","0760712040014","0013409000335","36300416","0051000204721","0016229901141"));
//        res.add(create("Uruguay", new Locale("es", "UY"),"8076800376999","8019428000013","7730303009358"));
//        res.add(create("Vanuatu", new Locale("bi", "VU"),"3245390149701"));
//        res.add(create("Vietnam", new Locale("vi", "VN"),"8936079120276","8851978808158","8713600186211"));
//        res.add(create("Yemen", new Locale("ar", "YE"),"9501101320079"));
        return res;
    }
}
