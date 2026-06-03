package com.music.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.music.dto.UserDTO;
import com.music.model.RoomMember;
import jakarta.transaction.Transactional;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

	boolean existsByRoomIdAndUserId(Long roomId, Long userId);
	List<RoomMember> findByRoomId(Long roomId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM RoomMember rm WHERE rm.roomId = :roomId AND rm.userId = :userId")
	void deleteByRoomIdAndUserId(Long roomId, Long userId);
	
	@Query("""
	         SELECT new com.music.dto.UserDTO(u.id, u.username)
			 FROM User u
			 JOIN RoomMember rm ON u.id = rm.userId
			 WHERE rm.roomId = :roomId
	    """)
	List<UserDTO> findUsersByRoomId(@Param("roomId") Long roomId );
	
	Long countMembersByRoomId(@Param("roomId") Long roomId);
	Optional<RoomMember> findByRoomIdAndUserId(Long roomId, Long userId);
}
