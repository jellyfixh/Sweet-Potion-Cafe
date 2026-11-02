package com.potioncraft.test;

import java.awt.Font;

public class ThaiDisplayTest {
    public static void main(String[] args) {
        String[] samples = {
            "ร้านน้ำยาวิเศษ",
            "ทอม (ชาวบ้านสวนสมุนไพร)",
            "\"ขอโพชั่นเพิ่มพลังหน่อยนะ เมื่อเช้าทำสวนเหนื่อยเหลือเกิน\"",
            "ความอดทน: 23.5 วินาที",
            "เสิร์ฟน้ำยา (Serve)",
            "ปรุงโอสถ (BREW)",
            "ล้างหม้อ (Clear)",
            "ยินดีต้อนรับสู่ร้าน Potion Craft!"
        };

        Font font = new Font("Tahoma", Font.PLAIN, 14);
        boolean allPassed = true;
        for (String s : samples) {
            int failIndex = font.canDisplayUpTo(s);
            if (failIndex != -1) {
                System.out.println("FAILED on: " + s + " at index " + failIndex);
                allPassed = false;
            }
        }
        if (allPassed) {
            System.out.println("ALL THAI SAMPLES 100% SUPPORTED BY TAHOMA!");
        }
    }
}
