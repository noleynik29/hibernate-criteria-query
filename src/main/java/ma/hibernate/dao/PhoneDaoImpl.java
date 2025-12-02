package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error creating phone", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
        Root<Phone> root = cq.from(Phone.class);
        List<Predicate> predicates = new ArrayList<>();
        if (params.containsKey("countryManufactured")) {
            String[] countries = params.get("countryManufactured");
            if (countries != null && countries.length > 0) {
                predicates.add(root.get("countryManufactured").in((Object[]) countries));
            }
        }
        if (params.containsKey("maker")) {
            String[] makers = params.get("maker");
            if (makers != null && makers.length > 0) {
                predicates.add(root.get("maker").in((Object[]) makers));
            }
        }
        if (params.containsKey("color")) {
            String[] colors = params.get("color");
            if (colors != null && colors.length > 0) {
                predicates.add(root.get("color").in((Object[]) colors));
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(cq).getResultList();
    }

}
