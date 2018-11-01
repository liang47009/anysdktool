/**
 * Copyright (C) 2018 Ryszard Wiśniewski <brut.alll@gmail.com>
 * Copyright (C) 2018 Connor Tumbleson <connor.tumbleson@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yunfeng.anysdk.tool;

import brut.androlib.*;
import brut.androlib.err.CantFindFrameworkResException;
import brut.androlib.err.InFileNotFoundException;
import brut.androlib.err.OutDirExistsException;
import brut.common.BrutException;
import brut.directory.DirectoryException;
import brut.util.AaptManager;
import com.android.apksigner.ApkSignerTool;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.*;

/**
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 * @author Connor Tumbleson <connor.tumbleson@gmail.com>
 */
public class Main {
    final static String gameFolder = "G:\\amber2\\03_tools\\AndriodPackage\\Release\\GameFolder";
    final static String gameApk = "G:\\amber2\\03_tools\\AndriodPackage\\Release\\GameApk\\product_anysdk-release.apk";

    final static String channelFolder = "G:\\amber2\\03_tools\\AndriodPackage\\Release\\ChannelFolder";
    final static String channelApk = "G:\\amber2\\03_tools\\AndriodPackage\\Release\\ChannelApk\\AnySDK_haima.apk";

    final static String UniteFolder = "G:\\amber2\\03_tools\\AndriodPackage\\Release\\UniteFolder";
    final static String UniteApk = "G:\\amber2\\03_tools\\AndriodPackage\\Release\\UniteApk";

    //d -f apk_check_unsigned.apk -o H:\xll\software\apktool\apk_check_unsigned
    public static void main(String[] args) throws Exception {
//        GameConfig gameConfig = new GameConfig();
//        gameConfig.setGameDir("G:\\amber2\\03_tools\\AndriodPackage\\Release\\GameApk\\product_anysdk-release.apk");
//        gameConfig.setGameOutDir("G:\\amber2\\03_tools\\AndriodPackage\\Release\\GameFolder");
//        gameConfig.setChannelDir("G:\\amber2\\03_tools\\AndriodPackage\\Release\\ChannelApk\\AnySDK_haima.apk");
//        gameConfig.setChannelOutDir("G:\\amber2\\03_tools\\AndriodPackage\\Release\\ChannelFolder");
//        gameConfig.setUnitDir("G:\\amber2\\03_tools\\AndriodPackage\\Release\\UniteFolder");
//        gameConfig.setUnitOutDir("G:\\amber2\\03_tools\\AndriodPackage\\Release\\UniteApk");
//        IniConfig iniConfig = new IniConfig();
//        gameConfig.setIniConfig(iniConfig);
//        Map<String, Config> configs = new HashMap<String, Config>(8);
//        iniConfig.setConfigs();

        decode(gameApk, gameFolder);
        decode(channelApk, channelFolder);
        mergeFolder(channelFolder, gameFolder);
        encodeApk(gameFolder, UniteApk + "\\new.apk");
        signApk(UniteApk + "\\new.apk");
        optimizeApk(UniteApk + "\\Signed.apk");
    }

    /**
     * 优化apk
     *
     * @param signedApk apk路径
     * @throws IOException
     */
    private static void optimizeApk(String signedApk) throws IOException {
        URL url = Main.class.getClassLoader().getResource("zipalign.exe");
        if (null == url) {
            System.err.println("zipalign.exe not found!");
        } else {
            String fileName = url.getFile();
            File file = new File(fileName);
            if (file.exists()) {
                String command = fileName + " -v 4 " + signedApk + " " + UniteApk + "\\Zipalign.apk";
                Utils.openExe(command);
            } else {
                System.err.println("zipalign.exe not found!");
            }
        }
    }

    /**
     * 签名
     *
     * @param apkFullName apk全名包含路径
     */
    private static void signApk(String apkFullName) throws Exception {
        // keytool -list -v -keystore debug.keystore -storepass android 查看信息
        //java –jar SignApk.jar yushan.keystore storepassword aliasname aliaspassword unsigned.apk Signed.apk
        //jarsigner -verbose -keystore file.keystore -signedjar fileold.apk filenew.apk file.keystore(file.keystorepass)
        //SignApk.jar 		：签名的jar，填写绝对路径
        //yushan.keystore	：file.keystore，添加绝对路径
        //storepassword  	：keystore的密码
        //aliasname  		：别名名称
        //aliaspassword  	：别名密码
        //unsigned.apk		：签名前的apk名称，填写绝对路径
        //Signed.apk		：签名后的apk名称，填写绝对路径
//        apksigner sign --ks release.jks app.apk
//        apksigner verify --verbose app.apk
//        --ks                  Load private key and certificate chain from the Java
//        KeyStore initialized from the specified file. NONE means
//        no file is needed by KeyStore, which is the case for some
//        PKCS #11 KeyStores.
//
//        --ks-key-alias        Alias under which the private key and certificate are
//        stored in the KeyStore. This must be specified if the
//        KeyStore contains multiple keys.
//
//        --ks-pass             KeyStore password (see --ks). The following formats are
//        supported:
//        pass:<password> password provided inline
//        env:<name>      password provided in the named
//        environment variable
//        file:<file>     password provided in the named
//        file, as a single line
//        stdin           password provided on standard input,
//        as a single line
//        A password is required to open a KeyStore.
//        By default, the tool will prompt for password via console
//        or standard input.
//                When the same file (including standard input) is used for
//        providing multiple passwords, the passwords are read from
//        the file one line at a time. Passwords are read in the
//        order in which signers are specified and, within each
//        signer, KeyStore password is read before the key password
//        is read.
//
//        --key-pass            Password with which the private key is protected.
//        The following formats are supported:
        boolean useAndroidSigner = true;// 使用安卓原生apksigner
        if (useAndroidSigner) {
            String path = "";
            URL url = Main.class.getClassLoader().getResource("");
            if (null == url) {

            } else {
                String fileStr = url.getFile();
                path = fileStr + "debug.keystore";
            }
            String[] signApkArgs = new String[]{"sign", "--ks", path, "--ks-key-alias", "androiddebugkey", "--ks-pass", "pass:android", "--key-pass", "pass:android", apkFullName};
            ApkSignerTool.main(signApkArgs);
        } else {
            String[] signApkArgs = new String[]{"debug.keystore", "android", "androiddebugkey", "android", apkFullName, UniteApk + "\\Signed.apk"};
            SignApk.main(signApkArgs);
        }
    }

    /**
     * 合成apk
     *
     * @param gameFolder 文件夹名称
     * @param apkName    apk 名字
     * @throws Exception
     */
    private static void encodeApk(String gameFolder, String apkName) throws Exception {
        String[] decodeChannelArgs = new String[]{"b", "-f", gameFolder, "-o", apkName};
        decodeWithCommandLine(decodeChannelArgs);
    }

    /**
     * 合并渠道反编译后的目录到游戏目录
     *
     * @param channelFolder 渠道目录
     * @param gameFolder    游戏目录
     */
    private static void mergeFolder(String channelFolder, String gameFolder) {
        File channelDir = new File(channelFolder);
        if (channelDir.exists() && channelDir.isDirectory()) {
            File[] files = channelDir.listFiles();
            if (files == null) {
                System.err.println("channel folder: " + channelFolder + " not found");
            } else {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (file.getName().equals("original")) {
                            continue;
                        }
                        if (!copyFile(file.getAbsolutePath(), gameFolder)) {
                            System.err.println("copy: " + file.getAbsolutePath() + " to " + gameFolder + " failed!");
                        }
                    } else if (file.getName().equals("AndroidManifest.xml")) {
                        //todo
                        Document channel = Utils.readDocument(file);

                    } else if (file.getName().equals("apktool.yml")) {
                        //todo

                    }
                }
            }
        }
    }

    /**
     * 文件复制
     *
     * @param resFilePath 文件路径
     * @param distFolder  复制保存路径
     */
    private static boolean copyFile(String resFilePath, String distFolder) {
        File resFile = new File(resFilePath);
        File distFile = new File(distFolder);
        try {
            if (resFile.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(resFile, distFile);
            } else if (resFile.isFile()) {
                FileUtils.copyFileToDirectory(resFile, distFile, true);
            }
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("file Backup error!");
            return false;
        }
    }

    private static void decode(String apk, String folder) throws Exception {
        String[] decodeChannelArgs = new String[]{"d", "-f", apk, "-o", folder};
        decodeWithCommandLine(decodeChannelArgs);
    }

    private static void decodeWithCommandLine(String[] args) throws Exception {

        // set verbosity default
        Verbosity verbosity = Verbosity.NORMAL;

        // cli parser
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;

        // load options
        _Options();

        try {
            commandLine = parser.parse(allOptions, args, false);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            usage();
            return;
        }

        // check for verbose / quiet
        if (commandLine.hasOption("-v") || commandLine.hasOption("--verbose")) {
            verbosity = Verbosity.VERBOSE;
        } else if (commandLine.hasOption("-q") || commandLine.hasOption("--quiet")) {
            verbosity = Verbosity.QUIET;
        }
        setupLogging(verbosity);

        // check for advance mode
        if (commandLine.hasOption("advance") || commandLine.hasOption("advanced")) {
            setAdvanceMode(true);
        }

        boolean cmdFound = false;
        for (String opt : commandLine.getArgs()) {
            if (opt.equalsIgnoreCase("d") || opt.equalsIgnoreCase("decode")) {
                cmdDecode(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("b") || opt.equalsIgnoreCase("build")) {
                cmdBuild(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("if") || opt.equalsIgnoreCase("install-framework")) {
                cmdInstallFramework(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("empty-framework-dir")) {
                cmdEmptyFrameworkDirectory(commandLine);
                cmdFound = true;
            } else if (opt.equalsIgnoreCase("publicize-resources")) {
                cmdPublicizeResources(commandLine);
                cmdFound = true;
            }
        }

        // if no commands ran, run the version / usage check.
        if (!cmdFound) {
            if (commandLine.hasOption("version")) {
                _version();
                System.exit(0);
            } else {
                usage();
            }
        }
    }

    private static void cmdDecode(CommandLine cli) throws AndrolibException {
        ApkDecoder decoder = new ApkDecoder();

        int paraCount = cli.getArgList().size();
        String apkName = cli.getArgList().get(paraCount - 1);
        File outDir;

        // check for options
        if (cli.hasOption("s") || cli.hasOption("no-src")) {
            decoder.setDecodeSources(ApkDecoder.DECODE_SOURCES_NONE);
        }
        if (cli.hasOption("d") || cli.hasOption("debug")) {
            System.err.println("SmaliDebugging has been removed in 2.1.0 onward. Please see: https://github.com/iBotPeaches/Apktool/issues/1061");
            System.exit(1);
        }
        if (cli.hasOption("b") || cli.hasOption("no-debug-info")) {
            decoder.setBaksmaliDebugMode(false);
        }
        if (cli.hasOption("t") || cli.hasOption("frame-tag")) {
            decoder.setFrameworkTag(cli.getOptionValue("t"));
        }
        if (cli.hasOption("f") || cli.hasOption("force")) {
            decoder.setForceDelete(true);
        }
        if (cli.hasOption("r") || cli.hasOption("no-res")) {
            decoder.setDecodeResources(ApkDecoder.DECODE_RESOURCES_NONE);
        }
        if (cli.hasOption("force-manifest")) {
            decoder.setForceDecodeManifest(ApkDecoder.FORCE_DECODE_MANIFEST_FULL);
        }
        if (cli.hasOption("no-assets")) {
            decoder.setDecodeAssets(ApkDecoder.DECODE_ASSETS_NONE);
        }
        if (cli.hasOption("k") || cli.hasOption("keep-broken-res")) {
            decoder.setKeepBrokenResources(true);
        }
        if (cli.hasOption("p") || cli.hasOption("frame-path")) {
            decoder.setFrameworkDir(cli.getOptionValue("p"));
        }
        if (cli.hasOption("m") || cli.hasOption("match-original")) {
            decoder.setAnalysisMode(true, false);
        }
        if (cli.hasOption("api") || cli.hasOption("api-level")) {
            decoder.setApi(Integer.parseInt(cli.getOptionValue("api")));
        }
        if (cli.hasOption("o") || cli.hasOption("output")) {
            outDir = new File(cli.getOptionValue("o"));
            decoder.setOutDir(outDir);
        } else {
            // make out folder manually using name of apk
            String outName = apkName;
            outName = outName.endsWith(".apk") ? outName.substring(0,
                    outName.length() - 4).trim() : outName + ".out";

            // make file from path
            outName = new File(outName).getName();
            outDir = new File(outName);
            decoder.setOutDir(outDir);
        }

        decoder.setApkFile(new File(apkName));

        try {
            decoder.decode();
        } catch (OutDirExistsException ex) {
            System.err
                    .println("Destination directory ("
                            + outDir.getAbsolutePath()
                            + ") "
                            + "already exists. Use -f switch if you want to overwrite it.");
            System.exit(1);
        } catch (InFileNotFoundException ex) {
            System.err.println("Input file (" + apkName + ") " + "was not found or was not readable.");
            System.exit(1);
        } catch (CantFindFrameworkResException ex) {
            System.err
                    .println("Can't find framework resources for package of id: "
                            + String.valueOf(ex.getPkgId())
                            + ". You must install proper "
                            + "framework files, see project website for more info.");
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Could not modify file. Please ensure you have permission.");
            System.exit(1);
        } catch (DirectoryException ex) {
            System.err.println("Could not modify internal dex files. Please ensure you have permission.");
            System.exit(1);
        } finally {
            try {
                decoder.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void cmdBuild(CommandLine cli) throws BrutException {
        String[] args = cli.getArgs();
        String appDirName = args.length < 2 ? "." : args[1];
        File outFile;
        ApkOptions apkOptions = new ApkOptions();

        // check for build options
        if (cli.hasOption("f") || cli.hasOption("force-all")) {
            apkOptions.forceBuildAll = true;
        }
        if (cli.hasOption("d") || cli.hasOption("debug")) {
            System.out.println("SmaliDebugging has been removed in 2.1.0 onward. Please see: https://github.com/iBotPeaches/Apktool/issues/1061");
            apkOptions.debugMode = true;
        }
        if (cli.hasOption("v") || cli.hasOption("verbose")) {
            apkOptions.verbose = true;
        }
        if (cli.hasOption("a") || cli.hasOption("aapt")) {
            apkOptions.aaptPath = cli.getOptionValue("a");
        }
        if (cli.hasOption("c") || cli.hasOption("copy-original")) {
            apkOptions.copyOriginalFiles = true;
        }
        if (cli.hasOption("p") || cli.hasOption("frame-path")) {
            apkOptions.frameworkFolderLocation = cli.getOptionValue("p");
        }

        // Temporary flag to enable the use of aapt2. This will tranform in time to a use-aapt1 flag, which will be
        // legacy and eventually removed.
        if (cli.hasOption("use-aapt2")) {
            apkOptions.useAapt2 = true;
        }
        if (cli.hasOption("o") || cli.hasOption("output")) {
            outFile = new File(cli.getOptionValue("o"));
        } else {
            outFile = null;
        }

        // try and build apk
        try {
            if (cli.hasOption("a") || cli.hasOption("aapt")) {
                apkOptions.aaptVersion = AaptManager.getAaptVersion(cli.getOptionValue("a"));
            }
            new Androlib(apkOptions).build(new File(appDirName), outFile);
        } catch (BrutException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    private static void cmdInstallFramework(CommandLine cli) throws AndrolibException {
        int paraCount = cli.getArgList().size();
        String apkName = cli.getArgList().get(paraCount - 1);

        ApkOptions apkOptions = new ApkOptions();
        if (cli.hasOption("p") || cli.hasOption("frame-path")) {
            apkOptions.frameworkFolderLocation = cli.getOptionValue("p");
        }
        if (cli.hasOption("t") || cli.hasOption("tag")) {
            apkOptions.frameworkTag = cli.getOptionValue("t");
        }
        new Androlib(apkOptions).installFramework(new File(apkName));
    }

    private static void cmdPublicizeResources(CommandLine cli) throws AndrolibException {
        int paraCount = cli.getArgList().size();
        String apkName = cli.getArgList().get(paraCount - 1);

        new Androlib().publicizeResources(new File(apkName));
    }

    private static void cmdEmptyFrameworkDirectory(CommandLine cli) throws AndrolibException {
        ApkOptions apkOptions = new ApkOptions();

        if (cli.hasOption("f") || cli.hasOption("force")) {
            apkOptions.forceDeleteFramework = true;
        }
        if (cli.hasOption("p") || cli.hasOption("frame-path")) {
            apkOptions.frameworkFolderLocation = cli.getOptionValue("p");
        }

        new Androlib(apkOptions).emptyFrameworkDirectory();
    }

    private static void _version() {
        System.out.println(Androlib.getVersion());
    }

    @SuppressWarnings("static-access")
    private static void _Options() {

        // create options
        Option versionOption = Option.builder("version")
                .longOpt("version")
                .desc("prints the version then exits")
                .build();

        Option advanceOption = Option.builder("advance")
                .longOpt("advanced")
                .desc("prints advance information.")
                .build();

        Option noSrcOption = Option.builder("s")
                .longOpt("no-src")
                .desc("Do not decode sources.")
                .build();

        Option noResOption = Option.builder("r")
                .longOpt("no-res")
                .desc("Do not decode resources.")
                .build();

        Option forceManOption = Option.builder()
                .longOpt("force-manifest")
                .desc("Decode the APK's compiled manifest, even if decoding of resources is set to \"false\".")
                .build();

        Option noAssetOption = Option.builder()
                .longOpt("no-assets")
                .desc("Do not decode assets.")
                .build();

        Option debugDecOption = Option.builder("d")
                .longOpt("debug")
                .desc("REMOVED (DOES NOT WORK): Decode in debug mode.")
                .build();

        Option analysisOption = Option.builder("m")
                .longOpt("match-original")
                .desc("Keeps files to closest to original as possible. Prevents rebuild.")
                .build();

        Option apiLevelOption = Option.builder("api")
                .longOpt("api-level")
                .desc("The numeric api-level of the file to generate, e.g. 14 for ICS.")
                .hasArg(true)
                .argName("API")
                .build();

        Option debugBuiOption = Option.builder("d")
                .longOpt("debug")
                .desc("Sets android:debuggable to \"true\" in the APK's compiled manifest")
                .build();

        Option noDbgOption = Option.builder("b")
                .longOpt("no-debug-info")
                .desc("don't write out debug info (.local, .param, .line, etc.)")
                .build();

        Option forceDecOption = Option.builder("f")
                .longOpt("force")
                .desc("Force delete destination directory.")
                .build();

        Option frameTagOption = Option.builder("t")
                .longOpt("frame-tag")
                .desc("Uses framework files tagged by <tag>.")
                .hasArg(true)
                .argName("tag")
                .build();

        Option frameDirOption = Option.builder("p")
                .longOpt("frame-path")
                .desc("Uses framework files located in <dir>.")
                .hasArg(true)
                .argName("dir")
                .build();

        Option frameIfDirOption = Option.builder("p")
                .longOpt("frame-path")
                .desc("Stores framework files into <dir>.")
                .hasArg(true)
                .argName("dir")
                .build();

        Option keepResOption = Option.builder("k")
                .longOpt("keep-broken-res")
                .desc("Use if there was an error and some resources were dropped, e.g.\n"
                        + "            \"Invalid config flags detected. Dropping resources\", but you\n"
                        + "            want to decode them anyway, even with errors. You will have to\n"
                        + "            fix them manually before building.")
                .build();

        Option forceBuiOption = Option.builder("f")
                .longOpt("force-all")
                .desc("Skip changes detection and build all files.")
                .build();

        Option aaptOption = Option.builder("a")
                .longOpt("aapt")
                .hasArg(true)
                .argName("loc")
                .desc("Loads aapt from specified location.")
                .build();

        Option aapt2Option = Option.builder()
                .longOpt("use-aapt2")
                .desc("Upgrades apktool to use experimental aapt2 binary.")
                .build();

        Option originalOption = Option.builder("c")
                .longOpt("copy-original")
                .desc("Copies original AndroidManifest.xml and META-INF. See project page for more info.")
                .build();

        Option tagOption = Option.builder("t")
                .longOpt("tag")
                .desc("Tag frameworks using <tag>.")
                .hasArg(true)
                .argName("tag")
                .build();

        Option outputBuiOption = Option.builder("o")
                .longOpt("output")
                .desc("The name of apk that gets written. Default is dist/name.apk")
                .hasArg(true)
                .argName("dir")
                .build();

        Option outputDecOption = Option.builder("o")
                .longOpt("output")
                .desc("The name of folder that gets written. Default is apk.out")
                .hasArg(true)
                .argName("dir")
                .build();

        Option quietOption = Option.builder("q")
                .longOpt("quiet")
                .build();

        Option verboseOption = Option.builder("v")
                .longOpt("verbose")
                .build();

        // check for advance mode
        if (isAdvanceMode()) {
            DecodeOptions.addOption(noDbgOption);
            DecodeOptions.addOption(keepResOption);
            DecodeOptions.addOption(analysisOption);
            DecodeOptions.addOption(apiLevelOption);
            DecodeOptions.addOption(noAssetOption);
            DecodeOptions.addOption(forceManOption);

            BuildOptions.addOption(debugBuiOption);
            BuildOptions.addOption(aaptOption);
            BuildOptions.addOption(originalOption);
            BuildOptions.addOption(aapt2Option);
        }

        // add global options
        normalOptions.addOption(versionOption);
        normalOptions.addOption(advanceOption);

        // add basic decode options
        DecodeOptions.addOption(frameTagOption);
        DecodeOptions.addOption(outputDecOption);
        DecodeOptions.addOption(frameDirOption);
        DecodeOptions.addOption(forceDecOption);
        DecodeOptions.addOption(noSrcOption);
        DecodeOptions.addOption(noResOption);

        // add basic build options
        BuildOptions.addOption(outputBuiOption);
        BuildOptions.addOption(frameDirOption);
        BuildOptions.addOption(forceBuiOption);

        // add basic framework options
        frameOptions.addOption(tagOption);
        frameOptions.addOption(frameIfDirOption);

        // add empty framework options
        emptyFrameworkOptions.addOption(forceDecOption);
        emptyFrameworkOptions.addOption(frameIfDirOption);

        // add all, loop existing cats then manually add advance
        for (Object op : normalOptions.getOptions()) {
            allOptions.addOption((Option) op);
        }
        for (Object op : DecodeOptions.getOptions()) {
            allOptions.addOption((Option) op);
        }
        for (Object op : BuildOptions.getOptions()) {
            allOptions.addOption((Option) op);
        }
        for (Object op : frameOptions.getOptions()) {
            allOptions.addOption((Option) op);
        }
        allOptions.addOption(apiLevelOption);
        allOptions.addOption(analysisOption);
        allOptions.addOption(debugDecOption);
        allOptions.addOption(noDbgOption);
        allOptions.addOption(forceManOption);
        allOptions.addOption(noAssetOption);
        allOptions.addOption(keepResOption);
        allOptions.addOption(debugBuiOption);
        allOptions.addOption(aaptOption);
        allOptions.addOption(originalOption);
        allOptions.addOption(verboseOption);
        allOptions.addOption(quietOption);
        allOptions.addOption(aapt2Option);
    }

    private static String verbosityHelp() {
        if (isAdvanceMode()) {
            return "[-q|--quiet OR -v|--verbose] ";
        } else {
            return "";
        }
    }

    private static void usage() {
        _Options();
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(120);

        // print out license info prior to formatter.
        System.out.println(
                "Apktool v" + Androlib.getVersion() + " - a tool for reengineering Android apk files\n" +
                        "with smali v" + ApktoolProperties.get("smaliVersion") +
                        " and baksmali v" + ApktoolProperties.get("baksmaliVersion") + "\n" +
                        "Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>\n" +
                        "Updated by Connor Tumbleson <connor.tumbleson@gmail.com>");
        if (isAdvanceMode()) {
            System.out.println("Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0)\n");
        } else {
            System.out.println("");
        }

        // 4 usage outputs (general, frameworks, decode, build)
        formatter.printHelp("apktool " + verbosityHelp(), normalOptions);
        formatter.printHelp("apktool " + verbosityHelp() + "if|install-framework [options] <framework.apk>", frameOptions);
        formatter.printHelp("apktool " + verbosityHelp() + "d[ecode] [options] <file_apk>", DecodeOptions);
        formatter.printHelp("apktool " + verbosityHelp() + "b[uild] [options] <app_path>", BuildOptions);
        if (isAdvanceMode()) {
            formatter.printHelp("apktool " + verbosityHelp() + "publicize-resources <file_path>", emptyOptions);
            formatter.printHelp("apktool " + verbosityHelp() + "empty-framework-dir [options]", emptyFrameworkOptions);
            System.out.println("");
        } else {
            System.out.println("");
        }

        // print out more information
        System.out.println(
                "For additional info, see: http://ibotpeaches.github.io/Apktool/ \n"
                        + "For smali/baksmali info, see: https://github.com/JesusFreke/smali");
    }

    private static void setupLogging(final Verbosity verbosity) {
        Logger logger = Logger.getLogger("");
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        LogManager.getLogManager().reset();

        if (verbosity == Verbosity.QUIET) {
            return;
        }

        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (getFormatter() == null) {
                    setFormatter(new SimpleFormatter());
                }

                try {
                    String message = getFormatter().format(record);
                    if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                        System.err.write(message.getBytes());
                    } else {
                        if (record.getLevel().intValue() >= Level.INFO.intValue()) {
                            System.out.write(message.getBytes());
                        } else {
                            if (verbosity == Verbosity.VERBOSE) {
                                System.out.write(message.getBytes());
                            }
                        }
                    }
                } catch (Exception exception) {
                    reportError(null, exception, ErrorManager.FORMAT_FAILURE);
                }
            }

            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }
        };

        logger.addHandler(handler);

        if (verbosity == Verbosity.VERBOSE) {
            handler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
        } else {
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return record.getLevel().toString().charAt(0) + ": "
                            + record.getMessage()
                            + System.getProperty("line.separator");
                }
            });
        }
    }

    private static boolean isAdvanceMode() {
        return advanceMode;
    }

    private static void setAdvanceMode(boolean advanceMode) {
        Main.advanceMode = advanceMode;
    }

    private enum Verbosity {
        NORMAL, VERBOSE, QUIET
    }

    private static boolean advanceMode = false;

    private final static Options normalOptions;
    private final static Options DecodeOptions;
    private final static Options BuildOptions;
    private final static Options frameOptions;
    private final static Options allOptions;
    private final static Options emptyOptions;
    private final static Options emptyFrameworkOptions;

    static {
        //normal and advance usage output
        normalOptions = new Options();
        BuildOptions = new Options();
        DecodeOptions = new Options();
        frameOptions = new Options();
        allOptions = new Options();
        emptyOptions = new Options();
        emptyFrameworkOptions = new Options();
    }
}
