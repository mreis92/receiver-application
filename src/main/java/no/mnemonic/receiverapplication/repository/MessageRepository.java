package no.mnemonic.receiverapplication.repository;

import no.mnemonic.receiverapplication.domain.ReceiverMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<ReceiverMessage, Integer> {
    List<ReceiverMessage> findAllByReceiverId(int receiverId);

    @Query(value = "SELECT r.receiverId, COUNT(r.receiverId) FROM ReceiverMessage AS r GROUP BY r.receiverId")
    List<Object[]> countReceiverMessagesByReceiverId();
}
