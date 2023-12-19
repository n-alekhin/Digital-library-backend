package com.springproject.core.Repository;

import com.springproject.core.model.Entity.BookFullInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFullInfoRepository extends JpaRepository<BookFullInfo, Long> {
}
