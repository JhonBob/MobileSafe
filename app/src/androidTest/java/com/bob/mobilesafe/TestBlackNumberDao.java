package com.bob.mobilesafe;

import android.content.Context;
import android.content.Loader;
import android.test.AndroidTestCase;

import com.bob.mobilesafe.dao.BlackNumberDao;
import com.bob.mobilesafe.domain.BlackNumberInfo;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/1/1.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private Context context;

    @Override
    protected void setUp() throws Exception {
        context=getContext();
        super.setUp();
    }

    public  void testAdd() throws Exception{
        BlackNumberDao blackNumberDao=new BlackNumberDao(context);
        Random random=new Random(8979);
        for(long i=1;i<200;i++){
            long number=13500000000l+i;
            blackNumberDao.add(number+"", String.valueOf(random.nextInt(3)+1));
        }
        //boolean result=blackNumberDao.add("13500000000", "1");
        //assertEquals(true, result);
    }
    public  void testdelete() throws Exception{
        BlackNumberDao blackNumberDao=new BlackNumberDao(context);
        boolean result=blackNumberDao.delete("13500000000");
        assertEquals(true, result);
    }
    public  void testUpdateMode() throws Exception{
        BlackNumberDao blackNumberDao=new BlackNumberDao(context);
        boolean result=blackNumberDao.updateMode("13500000000", "2");
        assertEquals(true, result);
    }

    public  void testfindBlackMode() throws Exception{
        BlackNumberDao blackNumberDao=new BlackNumberDao(context);
        String mode=blackNumberDao.findBlackMode("13500000000");
        System.out.println("模式:"+mode);
    }
    public  void testfindAll() throws Exception{
        BlackNumberDao blackNumberDao=new BlackNumberDao(context);
        List<BlackNumberInfo>infos=blackNumberDao.findAll();
        for(BlackNumberInfo info:infos){
            System.out.println(info.getNumber()+"----"+info.getMode());
        }
    }
}
