package com.example.vacation_system.repository;

import com.example.vacation_system.entity.User;
import com.example.vacation_system.entity.VacationRequest;
import com.example.vacation_system.entity.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

    List<VacationRequest> findByUser(User user);

    List<VacationRequest> findByUserOrderByCreatedAtDesc(User user);

    List<VacationRequest> findByStatus(VacationStatus status);

    List<VacationRequest> findByStatusOrderByCreatedAtDesc(VacationStatus status);

    @Query("SELECT vr FROM VacationRequest vr WHERE vr.status = 'APPROVED' " +
            "AND ((vr.startDate BETWEEN :startDate AND :endDate) " +
            "OR (vr.endDate BETWEEN :startDate AND :endDate) " +
            "OR (vr.startDate <= :startDate AND vr.endDate >= :endDate))")
    List<VacationRequest> findApprovedVacationsInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT vr FROM VacationRequest vr WHERE vr.user = :user AND vr.status = 'APPROVED' " +
            "AND ((vr.startDate BETWEEN :startDate AND :endDate) " +
            "OR (vr.endDate BETWEEN :startDate AND :endDate) " +
            "OR (vr.startDate <= :startDate AND vr.endDate >= :endDate))")
    List<VacationRequest> findUserApprovedVacationsInDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT vr FROM VacationRequest vr WHERE vr.status = 'APPROVED' " +
            "AND vr.startDate <= :date AND vr.endDate >= :date")
    List<VacationRequest> findApprovedVacationsOnDate(@Param("date") LocalDate date);

    // Usunąłem problematyczną metodę calculateUsedVacationDays
    // Logika zostanie przeniesiona do serwisu

    List<VacationRequest> findByUserAndStatus(User user, VacationStatus status);
}