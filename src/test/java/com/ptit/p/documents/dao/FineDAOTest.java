package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Fine;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FineDAOTest {

    private FineDAO fineDAO = new FineDAO();

    @Test
    public void testFindAll_Success() {
        List<Fine> result = fineDAO.findAll();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        
        boolean foundId1 = false;
        for (Fine f : result) {
            if (f.getId() == 1) {
                foundId1 = true;
                Assert.assertNotNull(f.getName());
                Assert.assertTrue(f.getFineRate() >= 0);
                break;
            }
        }
        Assert.assertTrue(foundId1);
    }
}
