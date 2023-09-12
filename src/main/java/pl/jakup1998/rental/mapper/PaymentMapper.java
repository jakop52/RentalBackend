package pl.jakup1998.rental.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jakup1998.rental.dto.PaymentDto;
import pl.jakup1998.rental.model.Payment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public PaymentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PaymentDto convertToDto(Payment payment) {
        return modelMapper.map(payment, PaymentDto.class);
    }

    public Payment convertToEntity(PaymentDto paymentDto) {
        return modelMapper.map(paymentDto, Payment.class);
    }

    public List<PaymentDto> convertToDtoList(List<Payment> payments) {
        return payments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
