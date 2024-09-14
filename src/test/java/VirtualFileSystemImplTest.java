import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.qq.VirtualFileSystemImpl;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VirtualFileSystemImplTest {

    private VirtualFileSystemImpl vfs;

    @BeforeEach
    public void setUp() throws IOException {
        String zipPath = "C:\\вуз\\конфигурационное управление\\test\\course.zip";
        vfs = new VirtualFileSystemImpl(zipPath);
    }

    @Test
    public void testLs() {
        List<String> files = vfs.ls();

        String[] required = new String[] {".gitignore", ".idea/", "course.iml", "course.iml", "out/", "src/"};

        for(String req: required){
            assertTrue(files.contains(req));
        }
    }

    @Test
    public void testPwd() {
        vfs.cd("out");
        vfs.cd("production");

        String pwd = vfs.pwd();

        assertEquals("course/out/production/", pwd);
    }

    @Test
    public void testCp() throws IOException {
        vfs.cp("course.iml", "out");
        vfs.cd("out");
        List<String> files = vfs.ls();

        assertTrue(files.contains("course.iml"));

    }

    @Test
    public void testCd() {
        vfs.cd("out");
        List<String> files = vfs.ls();
        String[] required = new String[] {"production/"};

        for(String req: required){
            assertTrue(files.contains(req));
        }
    }

    @Test
    public void testGetUptime() throws InterruptedException {
        long startTime = System.currentTimeMillis() - 1000;
        vfs.setStartTime(startTime);

        String uptime = vfs.uptime();
        assertTrue(uptime.matches("Uptime: [0-9]{2}:[0-9]{2}:[0-9]{2}"));
    }
}
