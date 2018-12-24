public class JarUtil {
    private String jarName;
    private String jarPath;
    public JarUtil(Class clazz) {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation()
                .getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        java.io.File jarFile = new java.io.File(path);
        this.jarName = jarFile.getName();
        java.io.File parent = jarFile.getParentFile();
        if (parent != null) {
            this.jarPath = parent.getAbsolutePath();
        }
    }
    public String getJarName() {
        try {
            return java.net.URLDecoder.decode(this.jarName, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getJarPath() {
        try {
            return java.net.URLDecoder.decode(this.jarPath, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
