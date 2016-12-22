package com.intershop.tool.architecture.report.cmd;

public class ArchitectureReportConstants
{
    public static final String ARG_OUTPUT_DIRECTORY = "reports";
    public static final String ARG_BASELINE = "baseline";
    public static final String ARG_IVYFILE = "ivy";
    public static final String ARG_CARTRIDGE_DIRECTORY = "cartridges";
    public static final String ARG_EXISTING_ISSUES_FILE = "issues";
    public static final String ARG_KEYS = "keyselector";

    public static final String KEY_API_VIOLATION = "com.intershop.api.violation";
    public static final String KEY_XSS = "com.intershop.isml.xss";
    public static final String KEY_BO_INTERNAL = "com.intershop.businessobject.internal";
    public static final String KEY_BO_PERSISTENCE = "com.intershop.businessobject.persistence";
    public static final String KEY_PIPELET_UNUSED = "com.intershop.unused.pipelet";
    public static final String KEY_PIPELET_USED_DEPRECATED = "com.intershop.pipelet.used.deprecated";
    public static final String KEY_INVALID_PIPELINEREF = "com.intershop.pipeline.invalid.pipelineref";
}