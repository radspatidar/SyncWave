package com.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.music.model.RoomMember;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

	boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
