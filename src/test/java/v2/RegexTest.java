package v2;

import org.junit.Assert;
import me.isaac.audit.protocol_v2.convertor.MysqlDataTypeUtil;
import me.isaac.audit.util.RegexUtil;
import org.junit.Test;

public class RegexTest {
    @Test
    public void testInt() {
        Assert.assertTrue(RegexUtil.isInt("int<1>"));
        Assert.assertTrue(RegexUtil.isInt("int<8>"));
        Assert.assertTrue(RegexUtil.isInt("int<lenenc>"));
        Assert.assertFalse(RegexUtil.isInt("int<8lenenc>"));
        Assert.assertFalse(RegexUtil.isInt("int<null>"));
    }

    @Test
    public void testString() {
        Assert.assertTrue(RegexUtil.isString("string<lenenc>"));
        Assert.assertTrue(RegexUtil.isString("string<fix>"));
        Assert.assertTrue(RegexUtil.isString("string<var>"));
        Assert.assertTrue(RegexUtil.isString("string<EOF>"));
        Assert.assertTrue(RegexUtil.isString("string<NUL>"));
        Assert.assertFalse(RegexUtil.isString("string<1>"));
        Assert.assertFalse(RegexUtil.isString("string<$>"));
    }

    @Test
    public void dataTypeTest() {
        Assert.assertEquals(3, MysqlDataTypeUtil.getIntFixedLength("int<3>"));
        Assert.assertEquals(MysqlDataTypeUtil.getStringFixedLength("string<5>"), 5);
        Assert.assertEquals(MysqlDataTypeUtil.getStringFixedLength("string<15>"), 15);
    }
}
