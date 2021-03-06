package com.test.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.test.dao.CustomerDAO;
import com.test.dao.CustomerForSalesDAO;
import com.test.dao.FireInsuranceDAOimpl;
import com.test.dao.FireProposalDAOimpl;
import com.test.dao.InjuryInsuranceDAOimpl;
import com.test.dao.InjuryProposalDAOimpl;
import com.test.dao.VehicleInsuranceDAOimpl;
import com.test.dao.VehicleProposalDAOimpl;
import com.test.dto.Customer;
import com.test.dto.CustomerForSales;
import com.test.dto.FireInsurance;
import com.test.dto.FireProposal;
import com.test.dto.InjuryInsurance;
import com.test.dto.InjuryProposal;
import com.test.dto.Insurance;
import com.test.dto.Proposal;
import com.test.dto.Salesman;
import com.test.dto.VehicleInsurance;
import com.test.dto.VehicleProposal;
import com.test.enumeration.Bank;
import com.test.enumeration.CompensationType;
import com.test.enumeration.FacilityBusinessType;
import com.test.enumeration.FacilityMaterialType;
import com.test.enumeration.IllHistory;
import com.test.enumeration.Job;
import com.test.enumeration.PaymentType;
import com.test.enumeration.VehiclePurpose;
import com.test.enumeration.VehicleType;

/**
 * @author Administrator
 * @version 1.0
 * @created 12-5-2020 ���� 4:22:13
 */
@Controller
@SessionAttributes({"customer", "salesman"})
public class SalesController {
	
	@Autowired
	private FireInsuranceDAOimpl fireInsuranceDAOimpl;
	
	@Autowired
	private InjuryInsuranceDAOimpl injuryInsuranceDAOimpl;
	
	@Autowired
	private VehicleInsuranceDAOimpl vehicleInsuranceDAOimpl;
	
	@Autowired
	private FireProposalDAOimpl fireProposalDAOimpl;

	@Autowired
	private InjuryProposalDAOimpl injuryProposalDAOimpl;

	@Autowired
	private VehicleProposalDAOimpl vehicleProposalDAOimpl;
	
	@Autowired
	private CustomerForSalesDAO customerForSalesDAO;
	
	@Autowired
	private CustomerDAO customerDAO;
	
	@RequestMapping({ "/insuranceList" }) // 모든 보험 상품 보기
	public String showAllInsurnace(Model model, HttpSession session) {
		Salesman salesman = (Salesman) session.getAttribute("salesman");
		
		if (salesman == null) {
			System.out.println("로그인을 해주세요~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			return "redirect:logout";

		} else {
			List<Proposal> fireProposalList = new ArrayList<Proposal>();
			List<Proposal> injuryProposalList = new ArrayList<Proposal>();
			List<Proposal> vehicleProposalList = new ArrayList<Proposal>();
			
			List<Insurance> fireInsuranceList = this.fireInsuranceDAOimpl.showAllInsurance();
			for(Insurance fi : fireInsuranceList) {
				FireInsurance castFi = (FireInsurance) fi;
				Proposal prop = this.fireProposalDAOimpl.showSpecificProposal(castFi.getFireProposalID());
				if(prop != null) { // prop != null
					fireProposalList.add(prop);
				}else {
					FireProposal emptyProp = new FireProposal();
					emptyProp.setFireProposalID(-1);
					emptyProp.setProposalName("없음_오류_화재보험제안서");
					fireProposalList.add(emptyProp);
				}
			}
			
			List<Insurance> injuryInsuranceList = this.injuryInsuranceDAOimpl.showAllInsurance();
			for(Insurance ii : injuryInsuranceList) {
				InjuryInsurance castIi = (InjuryInsurance) ii;
				Proposal prop = this.injuryProposalDAOimpl.showSpecificProposal(castIi.getInjuryProposalID());
				if(prop != null) { // prop != null
					injuryProposalList.add(prop);
				}else {
					InjuryProposal emptyProp = new InjuryProposal();
					emptyProp.setInjuryProposalID(-1);
					emptyProp.setProposalName("없음_오류_상해보험제안서");
					injuryProposalList.add(emptyProp);
				}
			}
			
			List<Insurance> vehicleInsuranceList = this.vehicleInsuranceDAOimpl.showAllInsurance();
			for(Insurance vi : vehicleInsuranceList) {
				VehicleInsurance castVi = (VehicleInsurance) vi;
				Proposal prop = this.vehicleProposalDAOimpl.showSpecificProposal(castVi.getVehicleProposalID());
				if(prop != null) { // prop != null
					vehicleProposalList.add(prop);
				}else {
					VehicleProposal emptyProp = new VehicleProposal();
					emptyProp.setVehicleProposalID(-1);
					emptyProp.setProposalName("없음_오류_상해보험제안서");
					vehicleProposalList.add(emptyProp);
				}
			}
			
			model.addAttribute("fireInsuranceList", fireInsuranceList);
			model.addAttribute("injuryInsuranceList", injuryInsuranceList);
			model.addAttribute("vehicleInsuranceList", vehicleInsuranceList);
			
			model.addAttribute("fireProposalList", fireProposalList);
			model.addAttribute("injuryProposalList", injuryProposalList);
			model.addAttribute("vehicleProposalList", vehicleProposalList);
			
			return "joinInsurance/insuranceList";
		}
	}
	
	@RequestMapping(value = "/insuranceSalesInput") // 보험상품 상세보기
	public String showInsuranceDetail(Model model, String whichInsurance, int insuranceID) {
		FireInsurance finsurance = null;
		InjuryInsurance iinsurance = null;
		VehicleInsurance vinsurance = null;
		
		if(whichInsurance.equals("fire")) {
			finsurance = (FireInsurance) this.fireInsuranceDAOimpl.showInsuranceDetail(insuranceID);
			FireProposal fp = (FireProposal) this.fireProposalDAOimpl.showSpecificProposal(finsurance.getFireProposalID());
			model.addAttribute("proposal", fp);
			model.addAttribute("insurance", finsurance);
			model.addAttribute("facilityBusinessTypes", FacilityBusinessType.values());
			model.addAttribute("facilityMaterialTypes", FacilityMaterialType.values());
		}else if(whichInsurance.equals("injury")) {
			iinsurance = (InjuryInsurance)this.injuryInsuranceDAOimpl.showInsuranceDetail(insuranceID);
			InjuryProposal ip = (InjuryProposal) this.injuryProposalDAOimpl.showSpecificProposal(iinsurance.getInjuryProposalID());
			model.addAttribute("proposal", ip);
			model.addAttribute("insurance", iinsurance);
			model.addAttribute("illHistories", IllHistory.values());
			model.addAttribute("familyIllHistories", IllHistory.values());
		}else if(whichInsurance.equals("vehicle")) {
			vinsurance = (VehicleInsurance) this.vehicleInsuranceDAOimpl.showInsuranceDetail(insuranceID);
			VehicleProposal vp = (VehicleProposal) this.vehicleProposalDAOimpl.showSpecificProposal(vinsurance.getVehicleProposalID());
			model.addAttribute("proposal", vp);
			model.addAttribute("insurance", vinsurance);
			model.addAttribute("vehiclePurposes", VehiclePurpose.values());
			model.addAttribute("vehicleTypes", VehicleType.values());
		}else {
			System.out.println("~~NONE_insuranceDetail~~");
			return "redirect:/";
		}
		
		model.addAttribute("banks", Bank.values());
		model.addAttribute("jobs", Job.values());
		model.addAttribute("paymentTypes", PaymentType.values());
		model.addAttribute("compensationTypes", CompensationType.values());
		// Bank
		// PaymentType
		// CompensationType
		// Job // maybe common
		// -- common
		// FacilityBusinessType
		// FacilityMaterialType
		// -- fire
		// IllHistory
		// FamilyIllHistory // 없는 듯
		// -- injury
		// VehiclePurpose
		// VehicleType
		// -- vehicle
		
		return "joinInsurance/" + whichInsurance + "InsuranceInput";
		// /fire/injury/vechicle/InsuranceInput
	}
	
	@RequestMapping(value = "/calculateRate") // 보험상품 상세보기
	@ResponseBody
	public float calculateRate(Model model, @RequestParam HashMap<String, Object> rmap) {
		float rate = -1;
		
		String whichInsurance = (String) rmap.get("whichInsurance");
		int proposalID = Integer.parseInt((String) rmap.get("proposalID"));
		
		Proposal proposal = null;
		
		if(whichInsurance.equals("fire")) {
			FireProposal fp = (FireProposal) this.fireProposalDAOimpl.showSpecificProposal(proposalID);
			proposal = fp;
		}else if(whichInsurance.equals("injury")) {
			InjuryProposal ip = (InjuryProposal) this.injuryProposalDAOimpl.showSpecificProposal(proposalID);
			proposal = ip;
		}else if(whichInsurance.equals("vehicle")) {
			VehicleProposal vp = (VehicleProposal) this.vehicleProposalDAOimpl.showSpecificProposal(proposalID);
			proposal = vp;
		}
		
		UnderwritingTestStub underwritingTestStub = new UnderwritingTestStub();
		try {
			rate = underwritingTestStub.calculateRate(proposal, whichInsurance, rmap);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rate; // Ajax
	}
	
	@RequestMapping(value = "/saveCustomerForSales")
	public String saveCustomerForSales(Model model, HttpSession session, @RequestParam HashMap<String, Object> rmap) {
		
		rmap.put("salesmanID", 1); // forced login
		int lastInsertedCustomerForSalesID = this.customerForSalesDAO.insertCustomerForSales(rmap);
		System.out.println("^^^^" + lastInsertedCustomerForSalesID);
		
		CustomerForSales customerForSales = this.customerForSalesDAO.showCustomerForSalesById(lastInsertedCustomerForSalesID);
		
		int customerID = 0;
		
		// 주민등록번호로, 미리 가입되어있는 사람인지 확인해보기
		List<Customer> customers = this.customerDAO.showCustomerBySocialSecurityNumber(customerForSales.getCustomerForSalesSocialSecurityNumber());
		if(customers != null && customers.size() != 0) {
			// 회원인 경우
			if(customers.size() > 1) { // 2명 이상
				System.out.println("~~ duplicate socialSecurityNumber ~~");
				return "redirect:/";
			}
			System.out.println("[][]" + customers.toString());
			System.out.println("[][]" + customers.get(0).toString());
			customerID = customers.get(0).getCustomerID();
		}else {
			// 비회원인 경우
			customerID = customerForSales.getCustomerForSalesID();
			System.out.println("~~~*비회원*~~~");
			return "signUp";
		}
		
		String insuranceType = (String) rmap.get("whichInsurance");
		rmap.put("insuranceType", insuranceType);
		// int insuranceID = Integer.parseInt((String) rmap.get("insuranceID"));
		int recipientID = 1;
		rmap.put("recipientID", recipientID);
		
		int contractManagerID = 1;
		rmap.put("contractManagerID", contractManagerID);
		
		int insurancePaymentListID = 1;
		rmap.put("insurancePaymentListID", insurancePaymentListID);
		
		int salesmanID = 1;
		Salesman salesman = (Salesman) session.getAttribute("salesman");
		if(salesman != null) {
			salesmanID = salesman.getSalesmanID();
		}
		rmap.put("salesmanID", salesmanID);
		rmap.put("customerID", customerID);
		
		// insuranceType
		// insuranceID // FireInsurance, InjuryInsurance, VehicleInsurance
		// -- multiple foreign key
		// salesmanID
		// customerID --- 비회원이면 -1이 아니라, 방금 생성한 영업고객에서라도 특정!
		// -- should be multiple foreign key
		// recipientID
		// contractManagerID
		// insurancePaymentListID
		
		model.addAttribute("rmap", rmap);
		
		return "joinInsurance/aggreement";
	}
	
	
	@RequestMapping(value = "/showCustomerForSales") // 영업고객 조회하기
	public String showCustomerForSales(Model model, HttpSession session) {
		
		Salesman salesman = (Salesman) session.getAttribute("salesman");
		
		if (salesman == null) {
			System.out.println("로그인을 해주세요~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			return "redirect:logout";

		} else {
			int salesmanID = salesman.getSalesmanID();
			List<CustomerForSales> customerForSalesList = null;
			customerForSalesList = this.customerForSalesDAO.listCustomerForSalesBySalesmanID(salesmanID);
			
			model.addAttribute("customerForSalesList", customerForSalesList);
			
			
			
		return "showCustomerForSales";
		}
	}
	
	@RequestMapping(value = "/customerForSalesSearchResult") // 영업고객 조회하기
	public String customerForSalesSearchResult(Model model, HttpSession session, String opt, String term) {
		
		Salesman salesman = (Salesman) session.getAttribute("salesman");
		
		if (salesman == null) {
			System.out.println("로그인을 해주세요~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			return "redirect:logout";
			
		} else {
			int salesmanID = salesman.getSalesmanID();
		
			HashMap<String, Object> searchMap = new HashMap<String, Object>();
			searchMap.put("salesmanID", salesmanID);
			searchMap.put("term", term);
			
			List<CustomerForSales> customerForSalesList = null;
			
			if(opt.equals("0")) {// 고객명으로 검색한 경우
				System.out.println("이거슨 고객명이여~~~~~~~~~~~~~~~~~`");
				customerForSalesList= this.customerForSalesDAO.listCustomerForSalesByCustomerName(searchMap);
				
			}else if(opt.equals("1")){// 주민번호로 검색한 경우
				System.out.println("이거는 주민번호지롱~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				customerForSalesList= this.customerForSalesDAO.listCustomerForSalesBySalesSocialSecurityNumber(searchMap);
				
			}else {
				System.out.println("무엇으로 검색할지 선택을 안햇다능~");
				return "showCustomerForSales";
			}
			model.addAttribute("customerForSalesList", customerForSalesList);
			
			return "customerForSalesSearchResult";
		}
	}
	
}