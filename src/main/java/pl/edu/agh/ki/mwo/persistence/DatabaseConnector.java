package pl.edu.agh.ki.mwo.persistence;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import pl.edu.agh.ki.mwo.model.School;
import pl.edu.agh.ki.mwo.model.SchoolClass;
import pl.edu.agh.ki.mwo.model.Student;

public class DatabaseConnector {
	
	protected static DatabaseConnector instance = null;
	
	public static DatabaseConnector getInstance() {
		if (instance == null) {
			instance = new DatabaseConnector();
		}
		return instance;
	}
	
	Session session;

	protected DatabaseConnector() {
		session = HibernateUtil.getSessionFactory().openSession();
	}
	
	public void teardown() {
		session.close();
		HibernateUtil.shutdown();
		instance = null;
	}
	
/*	private void updateBackReferences() {
		String hql = "FROM School";
		Query query = session.createQuery(hql);
		List<School> schools = query.list();
		
		for (School s : schools) {
			for (SchoolClass sc : s.getClasses()) {
				sc.setSchool(s);
				for (Student st : sc.getStudents()) {
					st.setSchool(s);
					st.setSchoolClass(sc);
				}
			}
		}
	}*/
	
	public Iterable<School> getSchools() {
		//updateBackReferences();
		
		String hql = "FROM School";
		Query query = session.createQuery(hql);
		List<School> schools = query.list();
		
		return schools;
	}
	
	public void addSchool(School school) {
		Transaction transaction = session.beginTransaction();
		session.save(school);
		transaction.commit();
	}
	
	public void updateSchool(long id, String name, String address) {
		String hql = "FROM School S WHERE S.id=" + id;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		Transaction transaction = session.beginTransaction();
		School s = results.get(0);
		s.setName(name);
		s.setAddress(address);
		session.update(s);
		transaction.commit();
	}
	
	public void deleteSchool(String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		Transaction transaction = session.beginTransaction();
		School s = results.get(0);
		session.delete(s);
		transaction.commit();
	}

	public Iterable<SchoolClass> getSchoolClasses() {
		//updateBackReferences();

		String hql = "FROM SchoolClass";
		Query query = session.createQuery(hql);
		List<SchoolClass> schoolClasses = query.list();
				
		return schoolClasses;
	}
	
	public void addSchoolClass(SchoolClass schoolClass, String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		Transaction transaction = session.beginTransaction();
		School school = results.get(0);
		school.addClass(schoolClass);
		session.save(school);
		transaction.commit();
	}
	
	public void updateSchoolClass(long id, String profile, int startYear, int currentYear, long schoolId) {
		String hql = "FROM SchoolClass S WHERE S.id=" + id;
		String hql2 = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		Query query2 = session.createQuery(hql2);
		List<SchoolClass> results = query.list();
		List<School> results2 = query2.list();
		School school = null;
		if (results2.size() != 0) {
			school = results2.get(0);
		}
		Transaction transaction = session.beginTransaction();
		SchoolClass s = results.get(0);	
		School oldSchool = s.getSchool();
		s.setProfile(profile);
		s.setStartYear(startYear);
		s.setCurrentYear(currentYear);
		if (results2.size() != 0) {
			if (oldSchool != null) {
				oldSchool.getClasses().remove(s);
			}
			s.setSchool(school);
			school.addClass(s);
		}
		session.update(s);
		transaction.commit();
	}
	
	public void deleteSchoolClass(String schoolClassId) {
		String hql = "FROM SchoolClass S WHERE S.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> results = query.list();
		Transaction transaction = session.beginTransaction();
		for (SchoolClass s : results) {
			session.delete(s);
		}
		transaction.commit();
	}
	
	public Iterable<Student> getStudents() {
		//updateBackReferences();

		String hql = "FROM Student";
		Query query = session.createQuery(hql);
		List students = query.list();
				
		return students;
	}
	
	public void addStudent(Student student, String schoolClassId) {
		String hql = "FROM SchoolClass S WHERE S.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> results = query.list();
		Transaction transaction = session.beginTransaction();
		if (results.size() == 0) {
			session.save(student);
		} else {
			SchoolClass schoolClass = results.get(0);
			schoolClass.addStudent(student);
			session.save(schoolClass);
		}
		transaction.commit();
	}
	
	public void updateStudent(long id, String name, String surname, String pesel, long classId) {
		
		String hql = "FROM Student S WHERE S.id=" + id;
		String hql2 = "FROM SchoolClass S WHERE S.id=" + classId;
		Query query = session.createQuery(hql);
		Query query2 = session.createQuery(hql2);
		List<Student> results = query.list();
		List<SchoolClass> results2 = query2.list();
		SchoolClass schoolClass = null;
		if (results2.size() != 0) {
			schoolClass = results2.get(0);
		}
		Transaction transaction = session.beginTransaction();
		Student s = results.get(0);
		SchoolClass oldClass = s.getSchoolClass();
		s.setName(name);
		s.setSurname(surname);
		s.setPesel(pesel);
		if (results2.size() != 0) {
			if (oldClass != null) {
				oldClass.getStudents().remove(s);
			}
			s.setSchoolClass(schoolClass);
			schoolClass.addStudent(s);
			s.setSchool(schoolClass.getSchool());
		}
		session.update(s);
		transaction.commit();
		
	}
	
	public void deleteStudent(String studentId) {
		String hql = "FROM Student S WHERE S.id=" + studentId;
		Query query = session.createQuery(hql);
		List<Student> results = query.list();
		Transaction transaction = session.beginTransaction();
		Student s = results.get(0);
		session.delete(s);
		transaction.commit();
	}
}
