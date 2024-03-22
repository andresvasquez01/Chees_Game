$(document).ready(function(){
    $("#moveForm").submit(function(event){
        // Evitar que el formulario se envíe normalmente
        event.preventDefault();
        
        // Obtener los datos del formulario
        var formData = {
            from: $("#fromSquare").val(),
            to: $("#toSquare").val()
        };
        
        // Enviar la solicitud POST al servlet
        $.ajax({
            type: "POST",
            contentType: "application/x-www-form-urlencoded",
            url: "/play",
            data: formData,
            dataType: 'text',
            success: function(response){
                // Manejar la respuesta del servlet
                alert(response);
            },
            error: function(xhr, status, error) {
                // Manejar errores
                console.error(xhr.responseText);
            }
        });
    });
});
