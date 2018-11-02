package com.yunfeng.anysdk.tool;

import brut.androlib.meta.StringExConstructor;
import brut.androlib.meta.StringExRepresent;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class UsesFramework {
    public List<Integer> ids;
    public String tag;

    @Override
    public String toString() {
        return "UsesFramework{" +
                "ids=" + ids +
                ", tag='" + tag + '\'' +
                '}';
    }
}

class PackageInfo {
    public String forcedPackageId;
    public String renameManifestPackage;

    @Override
    public String toString() {
        return "PackageInfo{" +
                "forcedPackageId='" + forcedPackageId + '\'' +
                ", renameManifestPackage='" + renameManifestPackage + '\'' +
                '}';
    }
}

class VersionInfo {
    public String versionCode;
    public String versionName;

    @Override
    public String toString() {
        return "VersionInfo{" +
                "versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}

public class ApkToolYml {
    public String version;
    public String apkFileName;
    public boolean isFrameworkApk;
    public UsesFramework usesFramework;
    public Map<String, String> sdkInfo;
    public PackageInfo packageInfo;
    public VersionInfo versionInfo;
    public boolean compressionType;
    public boolean sharedLibrary;
    public boolean sparseResources;
    public Map<String, String> unknownFiles;
    public Collection<String> doNotCompress;

    private static Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        StringExRepresent representer = new StringExRepresent();
        PropertyUtils propertyUtils = representer.getPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        return new Yaml(new StringExConstructor(), representer, options);
    }

    public static ApkToolYml load(InputStream is) {
        return getYaml().loadAs(is, ApkToolYml.class);
    }

    public static ApkToolYml load(File file) throws IOException {
        InputStream fis = new FileInputStream(file);
        Throwable var2 = null;

        ApkToolYml var3;
        try {
            var3 = load(fis);
        } catch (Throwable var12) {
            var2 = var12;
            throw var12;
        } finally {
            if (fis != null) {
                if (var2 != null) {
                    try {
                        fis.close();
                    } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                    }
                } else {
                    fis.close();
                }
            }
        }
        return var3;
    }

    @Override
    public String toString() {
        return "ApkToolYml{" +
                "apkFileName='" + apkFileName + '\'' +
                ", compressionType=" + compressionType +
                ", doNotCompress=" + doNotCompress +
                ", isFrameworkApk=" + isFrameworkApk +
                ", packageInfo=" + packageInfo +
                ", sdkInfo=" + sdkInfo +
                ", sharedLibrary=" + sharedLibrary +
                ", sparseResources=" + sparseResources +
                ", unknownFiles=" + unknownFiles +
                ", usesFramework=" + usesFramework +
                ", version='" + version + '\'' +
                ", versionInfo=" + versionInfo +
                '}';
    }
}
