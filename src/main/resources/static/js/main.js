// Script para el efecto de header 
// Al hacer scroll (window.scrollY > 50) se agrega la clase .scrolled al header,
// Si el usuario vuelve arriba del todo, la clase se elimina y el header regresa a su estado base.
window.addEventListener('scroll', () => {
        const header = document.getElementById('main-header');
        // Validamos que el header exista antes de intentar agregar o eliminar clases
        if (window.scrollY > 50) {
                header.classList.add('scrolled');
        // Si el usuario vuelve arriba del todo, eliminamos la clase .scrolled
        } else {
                header.classList.remove('scrolled');
        }
});

// Navegación del carrusel "Nuestro equipo"
// Las flechas desplazan el carrusel horizontalmente una fracción de su ancho visible
const teamCarousel = document.getElementById('team-carousel');
const teamPrev = document.getElementById('team-prev');
const teamNext = document.getElementById('team-next');

// Validamos que los elementos del carrusel existan antes de agregar los event listeners
if (teamCarousel && teamPrev && teamNext) {
        const teamScrollStep = () => teamCarousel.clientWidth * 0.6;

        // Agregamos los event listeners a las flechas del carrusel para desplazarlo suavemente
        teamPrev.addEventListener('click', () => {
                teamCarousel.scrollBy({ left: -teamScrollStep(), behavior: 'smooth' });
        });

        // Agregamos el event listener a la flecha derecha del carrusel para desplazarlo suavemente
        teamNext.addEventListener('click', () => {
                teamCarousel.scrollBy({ left: teamScrollStep(), behavior: 'smooth' });
        });
}
