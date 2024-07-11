package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CustomerDao customerDao;

	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreId);

		copyPetStoreFields(petStore, petStoreData);

		PetStore savedPetStore = petStoreDao.save(petStore);
		return new PetStoreData(savedPetStore);
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore = new PetStore();

		if (Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}
		return petStore;

	}

	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("No pet store where ID=" + petStoreId));
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
	}

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		Long employeeId = petStoreEmployee.getEmployeeId();

		PetStore petStore = findPetStoreById(petStoreId);
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);

		employee = copyEmployeeFields(employee, petStoreEmployee);

		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);

		Employee savedEmployee = employeeDao.save(employee);

		return new PetStoreEmployee(savedEmployee);

	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		PetStore petStore = new PetStore();
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException("Employee with id=" + employeeId + " does not exist"));

		if (petStore.getPetStoreId() != petStoreId) {
			throw new IllegalArgumentException(
					"Pet Store where ID=" + petStore.getPetStoreId() + " does not match Pet Store ID:" + petStoreId);
		} else {
			return employee;
		}

	}

	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		Employee employee;

		if (Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {

			employee = findEmployeeById(petStoreId, employeeId);
		}
		return employee;
	}

	private Employee copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {

		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeePhoneNumber(petStoreEmployee.getEmployeePhoneNumber());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());

		return employee;
	}

	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		Long customerId = petStoreCustomer.getCustomerId();

		PetStore petStore = findPetStoreById(petStoreId);
		Customer customer = findOrCreateCustomer(petStoreId, customerId);

		customer = copyCustomerFields(customer, petStoreCustomer);

		customer.getPetStores().add(petStore);
		petStore.getCustomers().add(customer);

		Customer savedCustomer = customerDao.save(customer);

		return new PetStoreCustomer(savedCustomer);

	}

	private Customer findCustomerById(Long petStoreId, Long customerId) {
		PetStore petStore = new PetStore();
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer where ID=" + customerId + " does not exist"));

		if (petStore.getPetStoreId() != petStoreId) {
			throw new IllegalArgumentException(
					"Pet Store where ID=" + petStore.getPetStoreId() + " does not match Pet Store ID:" + petStoreId);
		} else {
			return customer;
		}

	}

	private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
		Customer customer;

		if (Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findCustomerById(petStoreId, customerId);
		}

		return customer;

	}

	private Customer copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
		return customer;
	}

	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStore> petStores = petStoreDao.findAll();

		List<PetStoreData> result = new LinkedList<>();

		for (PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);

			psd.getCustomers().clear();
			psd.getEmployees().clear();

			result.add(psd);
		}
		return result;

	}

	@Transactional(readOnly = true)
	public PetStoreData retrievePetStoreById(Long petStoreId) {
		PetStore results = findPetStoreById(petStoreId);
		return new PetStoreData(results);

	}

	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);

	}

}
