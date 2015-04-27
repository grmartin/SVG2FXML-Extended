import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.function.Predicate

import static OSUtils.fileURLToPath

class MyScript {
    public static String BASE_DIR_OVERRIDE_PROP = "a1a2.groovy.script"

    public static PropertyRegistry PROPERTY_REGISTRY;

    public static SUBDIRS = [
        ['e-fx-eclipse', new SVN('svg2xml', 'https://github.com/tomsontom/e-fx-clipse/trunk/at.bestsolution.efxclipse.formats.svg', ChronoUnit.DAYS.getDuration().multipliedBy(7))]
    ];

    static void main(String[] args) {

        File baseDir = null;

        if (MyScript.getClass().protectionDomain.codeSource == null) {

            String cmd = System.properties[BASE_DIR_OVERRIDE_PROP];

            if (cmd == null) {
                // Not all JVMs implement, but lets try it anyway.
                cmd = System.properties["sun.java.command"];

                if (cmd == null) {
                    throw new RuntimeException("Unable to location this script's base directory please provide one via the '$BASE_DIR_OVERRIDE_PROP' variable.")
                }
            }

            String[] paths = cmd.split(" ");
            String path = paths[paths.length - 1];
            baseDir = (path.contains("/") ? new File(path) : new File(System.properties["user.dir"] as String, path)).parentFile
        } else {
            baseDir = new File(MyScript.getClass().protectionDomain.codeSource.location.path).parentFile
        }

        PROPERTY_REGISTRY = new PropertyRegistry(new File(baseDir, ".update-config"))
        try {
            File curPath = baseDir
            System.out.println("Starting with path '" + curPath + "'");
            SUBDIRS.each { v ->
                curPath = baseDir

                v.each { i ->
                    if (i instanceof String) {
                        curPath = new File(curPath, i)
                    } else if (i instanceof Performance) {
                        if (!curPath.exists() && !curPath.mkdirs()) {
                            System.out.println("Unable to create path '" + curPath + "' and execute " + i);
                        } else {
                            System.out.println("Executing '" + i.getClass().simpleName + "' at " + curPath)
                            i.performOn(curPath)
                        }
                    } else {
                        System.out.println("Unable to execute " + i);
                    }

                }
            }
        } finally {
            PROPERTY_REGISTRY.close()
        }
    }
}

interface Performance {
    void performOn(File parent);
}

final class PropertyRegistry implements Closeable {
    private static final DTFORMAT = DateTimeFormatter.ofPattern("MMM d yyyy @ HH:mm");
    private static final DTSTRFORMAT = "This file was last updated at: %s (%s)";

    protected boolean dirty;
    protected File location;
    protected Properties props;
    protected Properties defaults;

    public PropertyRegistry(File file, Properties defaults = null) {
        this.location = file;
        this.defaults = defaults == null ? new Properties() : defaults;
        this.props = new Properties();
    }

    public synchronized String getProperty(String key, String absoluteFallback = null) {
        return this.props.getProperty(key, this.defaults.getProperty(key, absoluteFallback));
    }

    public synchronized void setProperty(String key, String value) {
        this.props.setProperty(key, value);
        dirty = true;
    }

    public synchronized void synchronize() {
        if (dirty) {
            ZoneId zoneId = TimeZone.getDefault().toZoneId();
            ZonedDateTime zonedDateTimeFromGregorianCalendar = new GregorianCalendar().toZonedDateTime();

            String out = zonedDateTimeFromGregorianCalendar.format(DTFORMAT);

            FileOutputStream fos = new FileOutputStream(location);
            props.store(fos, String.format(DTSTRFORMAT, out, zoneId));
            fos.flush();
            fos.close();

            dirty = false;
        }
    }

    @Override
    void close() throws IOException {
        synchronize();
        location = null;
        props = null;
    }
}

final class OSUtils {
    public static final String[] WINDOWS_EXEC_EXTENSIONS = ["exe", "bat", "com", "cmd", "pif"];

    public static boolean isWindows() {
        return System.properties['os.name'].toLowerCase().contains('windows');
    }

    public static String toWindowsExecutable(String name, String... exts = WINDOWS_EXEC_EXTENSIONS) {
        for (String ext : exts) {
            ProcessBuilder pb = new ProcessBuilder("which", String.format("%s.%s", name, ext))
            Process proc = pb.start()
            proc.waitFor();
            String s = inputToString(proc.getInputStream())

            if (s != null && s.trim().length() > 0) return s;
        }

        return null;
    }

    public static String toNXExecutable(String name) {
        ProcessBuilder pb = new ProcessBuilder("which", name)
        Process proc = pb.start()
        proc.waitFor();
        return inputToString(proc.getInputStream())
    }

    public static String inputToString(InputStream is) {
        Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name());
        return scanner.useDelimiter("\\A").next();
    }

    public static String fileURLToPath(URL url) {
        return new File(url.toURI()).absolutePath
    }
}

abstract class SCM implements Performance {
    private static final DTFORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final ZonedDateTime MOONLANDING = ZonedDateTime.of(1969, 07, 20, 20, 18, 0, 0, ZoneId.of("UTC"));
    private static final String TIMESTAMP = "ts";

    protected final String directory;
    protected final URL url;
    protected final Duration duration;

    protected URL execLocation;

    protected Predicate<URL> cloneCheckPredicate = new Predicate<URL>(){
        @Override boolean test(URL url) {
            return new File(url.path).exists();
        }
    };

    protected Predicate<URL> validCheckPredicate;

    public SCM(String directory, String url, duration = null) {
        this.directory = directory
        this.url = new URL(url);
        this.duration = duration == null ? ChronoUnit.MILLIS.duration.multipliedBy(0) : duration;
    }

    protected URL pathURL(String onelvlup) {
        return new File(onelvlup, directory).absoluteFile.toURI().toURL();
    }

    protected URL findExec(final String shortname, String windowsExt = 'exe') {
        if (execLocation != null) return execLocation;

        String execPath = OSUtils.isWindows() ? OSUtils.toWindowsExecutable(shortname, windowsExt) : OSUtils.toNXExecutable(shortname);

        if (execPath) {
            if (OSUtils.isWindows()) {
                // Do we have to handle the drive specifier? For now, lets assume not... if you use windows this is as far as i go.
                execPath = execPath.replace('\\', '/');
            }

            execLocation = new URL("file://" + execPath.trim());

            System.out.println("Found SCM Executable at: "+execLocation.toString());

            return execLocation
        }

        throw new RuntimeException("Unable to locate executable '"+shortname+"', Please install the application and ensure it is in your path before continuing.");
    }

    public boolean isCloned(String onelvlup) {
        return cloneCheckPredicate.test(pathURL(onelvlup));
    }

    public boolean isValidWC(String onelvlup) {
        return validCheckPredicate.test(pathURL(onelvlup));
    }


    protected String getPropPrefix(String suffix) {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] digest  = sha1.digest(this.directory.getBytes());
        return String.format('repo.%s.hash_%s.%s', this.getClass().getSimpleName(), new  BigInteger(1, digest).toString(16), suffix);
    }

    public abstract void cloneWC(String onlvlup);
    public abstract void updateWC(String onlvlup);

    public void removeWC(String onlvlup) {
        Path dir = FileSystems.getDefault().getPath(onlvlup, directory)
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                Files.delete(f);
                return FileVisitResult.CONTINUE;
            }
            public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
                Files.delete(d);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void saveTime() {
        MyScript.PROPERTY_REGISTRY.setProperty(getPropPrefix(TIMESTAMP), DTFORMAT.format(LocalDateTime.now()))
    }

    public LocalDateTime readTime() {
        String str = MyScript.PROPERTY_REGISTRY.getProperty(getPropPrefix(TIMESTAMP), null)

        if (str == null) return MOONLANDING.toLocalDateTime();
        else LocalDateTime.for(DTFORMAT.parse(str));
    }

    public void downloadOrUpdate(String onelvlup) {
        if (isCloned(onelvlup)) {
            if (!isValidWC(onelvlup)) {
                removeWC(onelvlup);
                cloneWC(onelvlup);
                saveTime();
            } else {
                updateWC(onelvlup);
            }
        } else {
            cloneWC(onelvlup);
            saveTime();
        }
    }

    public void performOn(File parent) {
        downloadOrUpdate(parent.absolutePath)
    }
}

class SVN extends SCM {
    private static final String EXEC = "svn"

    SVN(String directory, String url, duration = null) {
        super(directory, url, duration)

        validCheckPredicate = new Predicate<URL>() {
            @Override boolean test(URL inUrl) {
                ProcessBuilder pb = new ProcessBuilder(findExec(EXEC).getPath(), "info", fileURLToPath(inUrl))
                Process process = pb.start();
                int errCode = process.waitFor();

                return errCode == 0
            }
        }
    }

    @Override
    void cloneWC(String onlvlup) {
        ProcessBuilder pb = new ProcessBuilder(findExec(EXEC).getPath(), "co", url.toString(), fileURLToPath(pathURL(onlvlup)))
        Process process = pb.start();
        int errCode = process.waitFor();

        if (errCode != 0) {
            throw new RuntimeException("Unable to checkout repo '"+url.toString()+"'")
        }
    }

    @Override
    void updateWC(String onlvlup) {
        ProcessBuilder pb = new ProcessBuilder(findExec(EXEC).getPath(), "update", fileURLToPath(pathURL(onlvlup)))
        Process process = pb.start();
        int errCode = process.waitFor();

        if (errCode != 0) {
            if (LocalDateTime.now().minus(duration).isAfter(readTime())) {
                System.err.println("Unable to update repo '"+url.toString()+"', however this repo is not yet considered stale so we are continuing anyway.")
            } else {
                throw new RuntimeException("Unable to update repo '" + url.toString() + "'")
            }
        } else {
            saveTime();
        }
    }

}

//class GIT extends SCM {
//    GIT(String directory, String url, duration = null) {
//        super(directory, url, duration)
//
//        this.validCheckPredicate = new Predicate<URL>() {
//            @Override
//            boolean test(URL inUrl) {
//                return false
//            }
//        }
//    }
//
//    @Override
//    void cloneWC(String onlvlup) {
//
//    }
//
//    @Override
//    void updateWC(String onlvlup) {
//
//    }
//
//}
//
//class HG extends SCM {
//    HG(String directory, String url, duration = null) {
//        super(directory, url, duration)
//
//        this.validCheckPredicate = new Predicate<URL>() {
//            @Override
//            boolean test(URL inUrl) {
//                return false
//            }
//        }
//    }
//
//    @Override
//    void cloneWC(String onlvlup) {
//
//    }
//
//    @Override
//    void updateWC(String onlvlup) {
//
//    }
//
//}