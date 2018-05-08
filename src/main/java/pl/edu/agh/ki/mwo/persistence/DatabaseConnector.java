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
	
	public Iterable<School> getSchools() {
		String hql = "FROM School";
		Query query = session.createQuery(hql);
		List<School> schools = query.list();
		
		return schools;
	}
	
	public School getSchool(long id) {
		String hql = "FROM School S WHERE S.id=" + id;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		
		return results.get(0);
	}
	
	public void addSchool(School school) {
		Transaction transaction = session.beginTransaction();
		session.save(school);
		transaction.commit();
	}
	
	public void updateSchool(long id, String name, String address) {
		Transaction transaction = session.beginTransaction();
		School s = getSchool(id);
		s.setName(name);
		s.setAddress(address);
		session.update(s);
		transaction.commit();
	}
	
	public void deleteSchool(long schoolId) {
		Transaction transaction = session.beginTransaction();
		School s = getSchool(schoolId);
		session.delete(s);
		transaction.commit();
	}

	public Iterable<SchoolClass> getSchoolClasses() {
		String hql = "FROM SchoolClass";
		Query query = session.createQuery(hql);
		List<SchoolClass> schoolClasses = query.list();
				
		return schoolClasses;
	}
	
	public SchoolClass getSchoolClass(long schoolClassId) {
		String hql = "FROM SchoolClass S WHERE S.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> results = query.list();

		return results.get(0);
	}
	
	public void addSchoolClass(SchoolClass schoolClass, long schoolId) {
		Transaction transaction = session.beginTransaction();
		School school = getSchool(schoolId);
		school.addClass(schoolClass);
		schoolClass.setSchool(school);
		session.save(school);
		transaction.commit();
	}
	
	public void updateSchoolClass(long id, String profile, int startYear, int currentYear, long schoolId) {
		Transaction transaction = session.beginTransaction();
		SchoolClass s = getSchoolClass(id);	
		School oldSchool = s.getSchool();
		School newSchool = getSchool(schoolId);
		s.setProfile(profile);
		s.setStartYear(startYear);
		s.setCurrentYear(currentYear);
		if (oldSchool != null) {
			oldSchool.getClasses().remove(s);
		}
		s.setSchool(newSchool);
		newSchool.addClass(s);
		session.update(s);
		session.update(newSchool);
		session.update(oldSchool);
		transaction.commit();
	}
	
	public void deleteSchoolClass(long schoolClassId) {
		Transaction transaction = session.beginTransaction();
		SchoolClass s = getSchoolClass(schoolClassId); 
		session.delete(s);
		transaction.commit();
	}
	
	public Iterable<Student> getStudents() {
		String hql = "FROM Student";
		Query query = session.createQuery(hql);
		List students = query.list();
				
		return students;
	}
	
	public Student getStudent(long studentId) {
		String hql = "FROM Student S WHERE S.id=" + studentId;
		Query query = session.createQuery(hql);
		List<Student> results = query.list();
		
		return results.get(0);
	}
	
	public void addStudent(Student student, long schoolClassId) {
		Transaction transaction = session.beginTransaction();
		SchoolClass schoolClass = getSchoolClass(schoolClassId);
		schoolClass.addStudent(student);
		student.setSchoolClass(schoolClass);
		student.setSchool(schoolClass.getSchool());
		session.save(schoolClass);
		transaction.commit();
	}
	
	public void updateStudent(long id, String name, String surname, String pesel, long classId) {
		Transaction transaction = session.beginTransaction();
		Student s = getStudent(id);
		SchoolClass newSchoolClass = getSchoolClass(classId);
		s.setName(name);
		s.setSurname(surname);
		s.setPesel(pesel);
		if (s.getSchoolClass() != null) {
			s.getSchoolClass().getStudents().remove(s);
		}
		s.setSchoolClass(newSchoolClass);
		newSchoolClass.addStudent(s);
		s.setSchool(newSchoolClass.getSchool());
		session.update(s);
		transaction.commit();
		
	}
	
	public void deleteStudent(long studentId) {
		Transaction transaction = session.beginTransaction();
		Student s = getStudent(studentId);
		session.delete(s);
		transaction.commit();
	}
}
