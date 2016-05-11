package com.one.factor.exam.core;

import com.one.factor.exam.entities.Grid;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HibernateManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateManager.class);

    private final SessionFactory sessionFactory;

    public HibernateManager() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    private Session getSession() {
        return sessionFactory.openSession();
    }

    /**
     *
     * @param lon
     * @param lat
     * @return distance error in meters for grid item that point belongs to
     */
    public int getDistanceError(double lon, double lat) {
        Session session = getSession();
        try {
            Grid grid = (Grid)session.createCriteria(Grid.class)
                    .add(Restrictions.eq("tileX", (int)lon))
                    .add(Restrictions.eq("tileY", (int)lat)).uniqueResult();
            return grid.getDistanceError();
        } catch (HibernateException e) {
            logger.error("Exception at getDistanceError : " + e);
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     *
     * @param clazz
     * @return is table empty for appropriate entity
     */
    public <T> boolean isEmpty(Class<T> clazz) {
        Session session = getSession();
        try {
            Integer count =  ((Number)getSession().createCriteria(clazz)
                    .setProjection(Projections.rowCount()).uniqueResult()).intValue();

            return count == 0;
        } catch (HibernateException e) {
            logger.error("Exception at getDistanceError : " + e);
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     *
     * @param lon
     * @param lat
     * @return count of user in same grid item that point
     */
    public long getCountPerSection(double lon, double lat) {
        Session session = getSession();
        try {
            Long count = (Long)session.createQuery("select count(*) as count from UserPosition where lon >= :minLon and lon < :maxLon and lat >= :minLat and lat < :maxLat")
                    .setParameter("minLon", (double)(int)lon).setParameter("maxLon", (double)(int)lon + 1)
                    .setParameter("minLat", (double)(int)lat).setParameter("maxLat", (double)(int)lat + 1).uniqueResult();
            return count != null ? count : -1;
        } catch (Exception e) {
            logger.error("Exception at getCountPerSection : ", e);
        } finally {
            session.close();
        }
        return -1;
    }

    public <T> T getById(int id, Class<T> clazz) {
        Session session = getSession();
        try {
            return getSession().get(clazz, id);
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Exception at getById : ", e);
        } finally {
            session.close();
        }
        return null;
    }

    public <T> boolean saveOrUpdate(T object) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(object);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Exception at saveOrUpdate : ", e);
            return false;
        } finally {
            session.close();
        }
        return true;
    }

    public boolean batchSave(List<?> list) {
        Session session = getSession();
        try {
            session.beginTransaction();
            for (Object o : list) {
                session.saveOrUpdate(o);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Exception at saveOrUpdate : ", e);
            return false;
        } finally {
            session.close();
        }
        return true;
    }

    public <T> boolean delete(T object) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.delete(object);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Exception at deleteObjectToDB : ", e);
            return false;
        } finally {
            session.close();
        }
        return true;
    }
}
