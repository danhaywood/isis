/**
 * Sends a PUT to /objects/oid/property/propertyId?proposedValue=valueOrOid
 */
function modifyProperty(objectUri, propertyId, proposedValue) {
	modifyAssociation("PUT", objectUri, "property", propertyId, proposedValue);
}

/**
 * Sends a DELETE to /objects/oid/property/propertyId
 */
function clearProperty(objectUri, propertyId, proposedValue) {
	modifyAssociation("DELETE", objectUri, "property", propertyId, null);
}

/**
 * Sends a PUT to /objects/oid/collection/collectionId?proposedValue=oidToAdd
 */
function addToCollection(objectUri, collectionId, proposedValue) {
	modifyAssociation("PUT", objectUri, "collection", collectionId, proposedValue);
}

/**
 * Sends a DELETE to /objects/oid/collection/collectionId?proposedValue=oidToRemove
 */
function removeFromCollection(objectUri, collectionId, proposedValue) {
	modifyAssociation("DELETE", objectUri, "collection", collectionId, proposedValue);
}

/**
 * Helper that factors out common functionality in the four functions that modify
 * either properties or collections.
 */
function modifyAssociation(verb, objectUri, associationType, associationId, proposedValue) {
	var uri = uriOnSuccess(objectUri, associationType, associationId);
	if (proposedValue) {
		uri += "?" + "proposedValue=" + proposedValue;
	}
	var reasonInvalidDomId = reasonInvalidDomIdOnFailure(associationType, associationId);
	invokeUri(verb, uri, objectUri, reasonInvalidDomId);
}



function uriOnSuccess(objectUri, memberType, memberId) {
	return objectUri + "/" + memberType + "/" + memberId;
}

function reasonInvalidDomIdOnFailure(memberType, memberId) {
	return memberType + "-invalid-" + memberId;
}

/**
 * Helper function that does the heavily lifting.
 * 
 * <p>
 * Makes the request, and redirects to the objectUri if successful, or updates the
 * DOM element with id <tt>association-invalid-xxx</tt> otherwise. 
 */
function invokeUri(verb, uri, uriOnSuccess, reasonInvalidDomIdOnFailure) {
	//debugger;
	var xhr = new XMLHttpRequest;
	xhr.open(verb, uri, false);
	xhr.setRequestHeader("Accept", "text/html");
	xhr.send(null);
	if (xhr.status >= 200 && xhr.status < 300) {
		window.location = uriOnSuccess;
	} else if (xhr.status >= 400 && xhr.status < 500) {
		// client error
		reason = xhr.getResponseHeader("nof-reason");
		if (!reason) {
			reason = "invalid (unable to determine reason)";
		}
		reasonInvalidPara = document.getElementById(reasonInvalidDomIdOnFailure);
		if (reasonInvalidPara) {
			reasonInvalidPara.innerHTML = reason;
		}
	} else {
		alert(xhr.status)
	}
	http_request = null;
}
