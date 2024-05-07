package com.springproject.core.Repository;

import com.springproject.core.model.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewRepository extends JpaRepository<Review, Long> {

}