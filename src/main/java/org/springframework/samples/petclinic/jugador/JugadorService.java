package org.springframework.samples.petclinic.jugador;

import java.security.Principal;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JugadorService {
    private JugadorRepository jugadorRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authoritiesService;
    
    @Autowired
    public JugadorService(JugadorRepository jugadorRepo){
        this.jugadorRepo = jugadorRepo;
    }

    @Transactional(readOnly = true)
    public List<Jugador> getJugadores(){
        return jugadorRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Jugador> getJugadorById(Integer id){
        return jugadorRepo.findById(id);
    }
    
    @Transactional
    public void deleteJugador(Integer id){
        jugadorRepo.deleteById(id);

    }

    @Transactional
    public void saveJugador(Jugador j) throws DataAccessException{
        jugadorRepo.save(j);

        userService.saveUser(j.getUser());

        authoritiesService.saveAuthorities(j.getUser().getUsername(),"Jugador");
    }

    @Transactional
    public void editJugador(Jugador j) throws DataAccessException{
        Jugador toUpdate = jugadorRepo.findById(j.getId()).get();
        toUpdate.setFirstName(j.getFirstName());
        toUpdate.setLastName(j.getLastName());
        toUpdate.getUser().setPassword(j.getUser().getPassword());
        jugadorRepo.save(toUpdate);
        userService.saveUser(toUpdate.getUser());
    }

    @Transactional
    public void agregarAmigo(Jugador j, Principal principal) throws DataAccessException{
        Jugador toUpdate = jugadorRepo.findById(j.getId()).get();
        List<Jugador> amigos = jugadorRepo.findJugadorByUsername(principal.getName()).getAmigoDe();
        amigos.add(j);
        jugadorRepo.save(toUpdate);
    }

    @Transactional
    public void deleteAmigo(String username1, String username2) throws DataAccessException{
        Jugador toUpdate = jugadorRepo.findJugadorByUsername(username1);
        List<Jugador> amigos = toUpdate.getAmigoDe();
        Jugador amigoABorrar = jugadorRepo.findJugadorByUsername(username2);
        amigos.remove(amigoABorrar);
        toUpdate.setAmigoDe(amigos);
        jugadorRepo.save(toUpdate);
    }

    @Transactional(readOnly = true)
    public Jugador getJugadorByUsername(String username){
        return jugadorRepo.findJugadorByUsername(username);
    }

    
}