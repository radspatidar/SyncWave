const API = window.location.origin;

function authHeaders() {
    return {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
    };
}

async function apiFetch(url, options = {}) {
	
	try{
		options.headers = {
		        ...authHeaders(),
		        ...(options.headers || {})
		    };
			
			console.log("TOKEN =", localStorage.getItem("token"));

		    const response = await fetch( API + url , options);
			
			console.log("STATUS =", response.status);
			console.log("URL =", response.url);

		    if (response.status === 401) {

		        localStorage.removeItem("token");

		        window.location.href = "/login.html";
				
				return;
		    }
			
		return response;
	} catch(error){
		console.error("API Error:",error);
	} 
}


async function apiFetchJson(url, options = {}) {
	
    const response = await apiFetch(url, options);
	
    if (!response) return null;
	
    return await response.json();
}