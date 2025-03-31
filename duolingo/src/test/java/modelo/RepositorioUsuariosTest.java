package modelo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistencia.UsuarioDAO;

@ExtendWith(MockitoExtension.class)
class RepositorioUsuariosTest {
 private RepositorioUsuarios repositorio;
 
 @Mock
 private UsuarioDAO usuarioDAO;
 
 @BeforeEach
 void setUp() {
     repositorio = RepositorioUsuarios.getUnicaInstancia();
     // Inyección manual del mock para pruebas
     repositorio.usuarioDAO = usuarioDAO;
 }

 @Test
 void testSingleton() {
     RepositorioUsuarios otraInstancia = RepositorioUsuarios.getUnicaInstancia();
     assertSame(repositorio, otraInstancia);
 }

 @Test
 void testBuscarPorEmail() {
     Usuario usuarioMock = mock(Usuario.class);
     when(usuarioDAO.buscarPorEmail("test@test.com")).thenReturn(usuarioMock);
     
     Usuario resultado = repositorio.buscarPorEmail("test@test.com");
     assertEquals(usuarioMock, resultado);
     verify(usuarioDAO).buscarPorEmail("test@test.com");
 }

 @Test
 void testGetUsuarios() {
     when(usuarioDAO.listarTodos()).thenReturn(List.of(new Usuario()));
     assertEquals(1, repositorio.getUsuarios().size());
 }
}
