package utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static utils.Butil.mandatoryValue;
import static utils.Butil.optionalValue;

class ButilTest {

   @Test
   void mandatoryValueValid() {

      String ok1 = mandatoryValue("ok1").get();
      assertEquals("ok1", ok1);

      Integer ok2 = mandatoryValue(111).get();
      assertEquals(111, ok2);

      int intFromBuilder = 10;
      Integer ok3 = mandatoryValue(intFromBuilder)
         .isNotNull("'intFromBuilder' must not be NULL")
         .isTrue(true, "'intFromBuilder' must be > 0")
         .isTrue(integer -> integer > 0, "'intFromBuilder' must be > 0")
         .isFalse(false, "'intFromBuilder' must be > 0")
         .isFalse(integer -> integer < 0, "'intFromBuilder' must be > 0")
         .get();
      assertEquals(10, ok3);

   }


   @Test
   void mandatoryValueInvalid() {
      assertThrows(IllegalArgumentException.class, () ->
         mandatoryValue(null).isNotNull("'intFromBuilder' must not be NULL"));

      assertThrows(IllegalArgumentException.class, () ->
         mandatoryValue(0).isTrue(false, "Passed boolean must be true"));

      assertThrows(IllegalArgumentException.class, () ->
         mandatoryValue(0).isFalse(true, "Passed boolean must be false"));

      assertThrows(IllegalArgumentException.class, () ->
         mandatoryValue(0).isTrue(integer -> integer > 0, "'intFromBuilder' must be > 0"));

      assertThrows(IllegalArgumentException.class, () ->
         mandatoryValue(1).isFalse(integer -> integer > 0, "'intFromBuilder' must be < 0"));

   }

   @Test
   void optionalValueValid() {

      String ok1 = optionalValue(null, "ok1").get();
      assertEquals("ok1", ok1);

      Integer ok2 = optionalValue(null, 111).get();
      assertEquals(111, ok2);

      int intFromBuilder = 10;
      Integer ok3 = optionalValue(null, intFromBuilder)
         .isNotNull("'intFromBuilder' must not be NULL")
         .isTrue(true, "'intFromBuilder' must be > 0")
         .isTrue(integer -> integer > 0, "'intFromBuilder' must be > 0")
         .isFalse(false, "'intFromBuilder' must be > 0")
         .isFalse(integer -> integer < 0, "'intFromBuilder' must be > 0")
         .get();
      assertEquals(10, ok3);

   }

   @Test
   void map() {
      String pathAsString = "/etc";
      Path path = mandatoryValue(pathAsString).map(Paths::get);
      assertEquals(path.toAbsolutePath().toString(), "/etc");
   }
}
