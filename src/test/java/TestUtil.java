import cn.hutool.core.util.HexUtil;
import me.isaac.audit.protocol.util.FixedLengthUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestUtil {
    @Test
    public void testHex() {
        byte[] bytes;
        bytes = HexUtil.decodeHex("fa0000");
        assert (FixedLengthUtil.readUB3(bytes) == 250);
        bytes = HexUtil.decodeHex("fcfb00");
        System.out.println(FixedLengthUtil.readUB3(bytes));
        assert (FixedLengthUtil.readUB3(bytes) == 251);
    }

    @Test
    public void testMySQL() {
        //Connection conn = null;
        //Statement stmt = null;
        String DB_URL = "jdbc:mysql://127.0.0.1:9000/information_schema?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
        try (Connection conn = DriverManager.getConnection(DB_URL, "isaac", "isaac");
             Statement stmt = conn.createStatement()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("连接数据库...");
            //conn = DriverManager.getConnection(DB_URL, "root", "ldsk1234");
            //stmt=conn.createStatement();
            String sql = "SELECT * FROM PROCESSLIST LIMIT 1";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String user = rs.getString("USER");
                String host = rs.getString("HOST");

                System.out.printf("ID=%d,USER=%s,HOST=%s%n", id, user, host);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHex2() {
        int[] arr = new int[]{
                0x4e, 0x00, 0x00, 0x00, 0x0a, 0x35, 0x2e, 0x36, 0x2e, 0x35, 0x30, 0x2d, 0x6c, 0x6f, 0x67, 0x00,
                0xf6, 0x7c, 0x0c, 0x00, 0x7c, 0x2f, 0x70, 0x68, 0x39, 0x22, 0x3f, 0x54, 0x00, 0xff, 0xf7, 0x2e,
                0x02, 0x00, 0x7f, 0x80, 0x15, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x6a,
                0x38, 0x76, 0x6c, 0x3c, 0x26, 0x48, 0x75, 0x37, 0x4f, 0x69, 0x41, 0x00, 0x6d, 0x79, 0x73, 0x71,
                0x6c, 0x5f, 0x6e, 0x61, 0x74, 0x69, 0x76, 0x65, 0x5f, 0x70, 0x61, 0x73, 0x73, 0x77, 0x6f, 0x72,
                0x64, 0x00};

        System.out.println((char) arr[5]);
        System.out.println((char) arr[6]);
    }
}
