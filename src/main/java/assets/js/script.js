/**
 * http://usejsdoc.org/
 */

$(function() {
	
	if(isICNumberExist) {
		$('#checkBtn').trigger('click');
	}
	
	var disableForm = function() {
		$('#spinnerBtn').show();
		$('#checkBtn').attr('disabled','disabled');
		$("#icTxt").attr('disabled','disabled');
	}
	
	var enableForm = function() {
		$('#spinnerBtn').hide();
		$('#checkBtn').removeAttr('disabled');
		$("#icTxt").removeAttr('disabled');
	}
	
	$('#checkBtn').click(function(){
		
		var icno = $("#icTxt").val();
		
		if(icno.replace(/ /g,"").length < 1) {
			$("#icTxt").focus();
			return false;
		}
		
		if(icno.length < 12) {
			$("#icTxt").focus();
			return false;
		}
		
		$('#timeOutError').hide();
		$('#noSummonResult').hide();
		disableForm();
		
		$.ajax({
		    type: "POST",
		    url: "/v1/pdrm/summon",
		    data: "ic_no=" + $("#icTxt").val(),
		    dataType: "json",
		    timeout: 30000,
		    error: function(e){
		    	enableForm();
		    	$('#timeOutError').show();
		    },
		    success: function (response) {
		    	
		    	// If summon found
		    	if(response.Status == true) {
		    		
		    		$('#userName').text(response.Name);
		    		$('#totalAmount').text('Total: RM ' + response.TotalAmount);
		    		
					var template = $.templates("#theTmpl");
					var htmlOutput = template.render(response.SummonData);
					$("#summonResult").html(htmlOutput);
					$("#summonResultTable").show();
					
					enableForm();
				}
		    	
		    	// If summon NOT found
		    	if(response.Status == false) {
		    		$("#summonResultTable").hide();
		    		if(response.StatusMessage == 'No summon found') {
		    			$("#summonResultTable").hide();
		    			$('#noSummonResult').show();
		    			enableForm();
		    		}
		    		if(response.ErrorMessage) {
		    			$('#timeOutError').show();
		    			enableForm();
		    		}
		    	}
		    }
		});
	});
	
	/*data = {
		"data" : {
			"Status" : true,
			"Author" : "alifaziz@gmail.com",
			"TotalAmount" : "600.00",
			"SummonData" : [ {
				"SummonsNo" : "02AL569818",
				"VehicleNo" : "WTS7636",
				"Blacklisted" : "YES",
				"OPSSikapEnforcement" : "NO",
				"Amount" : "300.00",
				"FinalAmount" : "300.00",
				"OriginalAmount" : "300.00",
				"OffenceDate" : "25-03-2012 13:40:00",
				"EnforcementDate" : "26-03-2012",
				"NonCompoundable" : "NO",
				"District" : "KELANG",
				"Offence" : "LOST CONTROL",
				"Location" : "JLN TELOK GADUNG"
			}, {
				"SummonsNo" : "02AR163733",
				"VehicleNo" : "BMD769",
				"Blacklisted" : "YES",
				"OPSSikapEnforcement" : "NO",
				"Amount" : "300.00",
				"FinalAmount" : "300.00",
				"OriginalAmount" : "300.00",
				"OffenceDate" : "07-07-2014 17:32:00",
				"EnforcementDate" : "08-07-2014",
				"NonCompoundable" : "NO",
				"District" : "IPD KLANG UTARA",
				"Offence" : "NO HELMET",
				"Location" : "LINGKARAN SULTAN ABD SAMAD"
			} ],
			"IcNumber" : "840116105325"
		}
	}
	
	var template = $.templates("#theTmpl");
	var htmlOutput = template.render(data.data.SummonData);
	$("#summonResult").html(htmlOutput);
	$("#summonResultTable").show();*/
});
